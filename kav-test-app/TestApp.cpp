#define WIN32_LEAN_AND_MEAN		
#define _WIN32_WINNT 0x0400

#include <Windows.h>
#include <queue>
#include <iostream>

#include "Stopper.h"

using namespace std;


//Максимальное кол-во запросов в очереди (возможно и не нужно)
const int KReqsCountLimit = 100000;

//Время выполнения - 30сек
const int KAppTTL = 30000;

//Дескрипторы событий 
HANDLE waitObjs[2]; 
#define NEED_STOP 0     //Для остановки потоков
#define NEED_PROCESS 1  //Для уведомления потоков о наличии в 
						//очереди не обработанных запросов


//Кол-во потоков обрабатывающих запросы
const unsigned int KThreadCount = 7;

class Request{};

typedef queue<Request*>  RequestQueue;

//Критическая секция для синхронизации обращений к очереди
CRITICAL_SECTION cs;

//Очередь не обработанных запросов
RequestQueue qRequests;

//Стоп-сигнал
Stopper stopper;

//Код потока выполняющего сбор запросов
DWORD WINAPI Collector(LPVOID param);

//Код потока обрабатываещего запрос
DWORD WINAPI Worker(LPVOID lpParameter);

// возвращает NULL, если объект stopSignal указывает на необходимость остановки,
// либо указатель на память, которую в дальнейшем требуется удалить
//!			Тривиальная реализация для отладки
Request* GetRequest(Stopper stopSignal) throw()
{
	//Проверяем событие
	if(stopSignal.isStopped())	
		return NULL;

	//Эмуляция бурной деятельности
	Sleep(rand()%200);

	return new Request;
}

// обрабатывает запрос, но память не удаляет, завершает обработку досрочно, если
// объект stopSignal указывает на необходимость остановки
//!			Тривиальная реализация для отладки
void ProcessRequest(Request* request, Stopper stopSignal) throw()
{
	//Проверяем событие
	if(stopSignal.isStopped())	
		return;
	//Эмуляция бурной деятельности
	Sleep(rand()%1000);
}
//

//
class App
{
public:
	App::App(void)
	{
		//Инициализация
		InitializeCriticalSection(&cs);
		//Создаем 2 события
		waitObjs[NEED_STOP]    = CreateEvent(NULL, TRUE, FALSE, NULL);	 //Сигнал (Пора остановится)		
		waitObjs[NEED_PROCESS] = CreateEvent(NULL, TRUE, FALSE, NULL);	 //Сигнал (в очереди есть задания)
		//Дескриптор события остановки отдаем стоп-сигналу
		stopper.setEventHandle(waitObjs[NEED_STOP]);
		
	}

	virtual App::~App(void)
	{
		//Закрываем дескрипторы событий
		CloseHandle(waitObjs[NEED_STOP]);
		CloseHandle(waitObjs[NEED_PROCESS]);
		
		//потоков
		for(int i = 0; i < KThreadCount; i++)	
			CloseHandle(m_threads[i]);

		//и крет секцию
		DeleteCriticalSection(&cs);
	}

	//Основной метод приложения
	int App::exec()
	{
		//Пускаем потоки обрабатывающие запросы
		startThreads();
	
		//Спим 30 секунд
		Sleep(KAppTTL);

		//Отправляем стоп-сигнал
		stopper.stop();

		//Ждем остановки потоков
		waitThreads();
	
		//Т.к. никто уже с очередью не работает, можно обойтись без использования КС
		//Удаляем из очереди не обработанные запросы
		while(!qRequests.empty())
		{
			delete qRequests.front();
			qRequests.pop();
		}

		return 0;
	}
private:
	//Дескрипторы потоков
	HANDLE m_threads[KThreadCount + 1];		
	HANDLE m_hTimer;

	//Запуск потоков
	void App::startThreads()
	{
		//Запускаем коллектор запросов
		m_threads[0] = CreateThread(NULL, 0, Collector, NULL, 0, NULL);

		//И потоки их обратывающие
		for(int i  = 1; i < KThreadCount + 1; i++)
			m_threads[i] = CreateThread(NULL, 0, Worker, NULL, 0, NULL);

		cout << "All threads started\n";
	}

	//Ждем завершения работы всех потоков
	void App::waitThreads()
	{
		DWORD result = WaitForMultipleObjects(KThreadCount+1,  m_threads,  TRUE,  INFINITE);
		switch(result)
		{
		case WAIT_OBJECT_0:
			//Все потоки успешно завершили свою работу
			break;
		case WAIT_ABANDONED_0:
			//Один или несколько потоков завершились не освободив ресурс
			break;
		}

		cout << "All threads stopped\n";
	}
};
DWORD WINAPI Collector(LPVOID param)
{
	while(TRUE)
	{
		//Новый запрос
		Request* pReq = GetRequest(stopper);
		if(pReq)
		{
			//Заходим в КС
			EnterCriticalSection(&cs);
			
			//Если очередь была пуста, необходимо 
			//отправить уведомление потокам
			bool needNotify = qRequests.empty();

			//Если не превышен лимит запросов в очереди
			if(qRequests.size() < KReqsCountLimit)
			{
				//Помещаем запрос в очередь
				qRequests.push(pReq);
			}
			
			cout << "Reqs wait for process: " << qRequests.size() << "\n";

			//Выходим из КС
			LeaveCriticalSection(&cs);

			//Оповестить потоки о поступлении задания
			if(needNotify)
				SetEvent(waitObjs[NEED_PROCESS]);

		}else{
			//GetRequest вернул NULL значит поток может прерывать свою работу
			break;
		}
	}
	return 0;
}

DWORD WINAPI Worker(LPVOID param)
{
	DWORD tid = GetCurrentThreadId();

	Request* pReq = NULL;
	DWORD result = 0;
	while(TRUE)
	{
		//Ждем разрешение начать обработку запросов		
		result = WaitForMultipleObjects(2, waitObjs, FALSE, INFINITE);
		
		//Сигнал от обьекта stopper о необходимости завершить работу
		if(result - WAIT_OBJECT_0 == 0)
		{		
			cout << "Thread tid[" << tid <<"] stoped by signal\n";
			//Прерываем работу потока
			break;

		//Сигнал о наличии в очереди запросов
		}else if(result - WAIT_OBJECT_0 == 1)
		{

			//Заходим в КС
			EnterCriticalSection(&cs);
			//Если в очереди есть запросы, извлекаем один
			if(!qRequests.empty())
			{
				pReq = qRequests.front();
				qRequests.pop();

				cout <<"Thread tid:["<<tid<<"] processed request\n";
			}else{
				//Если очередь пуста сбрасываем событие, чтобы остальные 
				//потоки перешли в режим ожидания,выходим из КС и 
				//сами переходим в режим ожидания
				LeaveCriticalSection(&cs);
				ResetEvent(waitObjs[NEED_PROCESS]);				
				continue; // while(TRUE)
			}

			//Выходим из КС
			LeaveCriticalSection(&cs);

			if(pReq)
			{
				//Берем очередной запрос из очереди и обрабатываем его
				ProcessRequest(pReq, stopper);

				//После обработки освобождаем память
				delete pReq;
				pReq = NULL;
			}			
		}
	}
	
	return 0;
}

//Обработка возможных ошибок в программе умышлено опущена
int main(int argc,char** argv)
{
	App* pApp = new App;	
	int result = pApp->exec();
	delete pApp;
	system("pause");
	return(result);
}
