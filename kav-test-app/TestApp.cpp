#define WIN32_LEAN_AND_MEAN		
#define _WIN32_WINNT 0x0400

#include <Windows.h>
#include <queue>
#include <iostream>

#include "Stopper.h"

using namespace std;


//������������ ���-�� �������� � ������� (�������� � �� �����)
const int KReqsCountLimit = 100000;

//����� ���������� - 30���
const int KAppTTL = 30000;

//����������� ������� 
HANDLE waitObjs[2]; 
#define NEED_STOP 0     //��� ��������� �������
#define NEED_PROCESS 1  //��� ����������� ������� � ������� � 
						//������� �� ������������ ��������


//���-�� ������� �������������� �������
const unsigned int KThreadCount = 7;

class Request{};

typedef queue<Request*>  RequestQueue;

//����������� ������ ��� ������������� ��������� � �������
CRITICAL_SECTION cs;

//������� �� ������������ ��������
RequestQueue qRequests;

//����-������
Stopper stopper;

//��� ������ ������������ ���� ��������
DWORD WINAPI Collector(LPVOID param);

//��� ������ ��������������� ������
DWORD WINAPI Worker(LPVOID lpParameter);

// ���������� NULL, ���� ������ stopSignal ��������� �� ������������� ���������,
// ���� ��������� �� ������, ������� � ���������� ��������� �������
//!			����������� ���������� ��� �������
Request* GetRequest(Stopper stopSignal) throw()
{
	//��������� �������
	if(stopSignal.isStopped())	
		return NULL;

	//�������� ������ ������������
	Sleep(rand()%200);

	return new Request;
}

// ������������ ������, �� ������ �� �������, ��������� ��������� ��������, ����
// ������ stopSignal ��������� �� ������������� ���������
//!			����������� ���������� ��� �������
void ProcessRequest(Request* request, Stopper stopSignal) throw()
{
	//��������� �������
	if(stopSignal.isStopped())	
		return;
	//�������� ������ ������������
	Sleep(rand()%1000);
}
//

//
class App
{
public:
	App::App(void)
	{
		//�������������
		InitializeCriticalSection(&cs);
		//������� 2 �������
		waitObjs[NEED_STOP]    = CreateEvent(NULL, TRUE, FALSE, NULL);	 //������ (���� �����������)		
		waitObjs[NEED_PROCESS] = CreateEvent(NULL, TRUE, FALSE, NULL);	 //������ (� ������� ���� �������)
		//���������� ������� ��������� ������ ����-�������
		stopper.setEventHandle(waitObjs[NEED_STOP]);
		
	}

	virtual App::~App(void)
	{
		//��������� ����������� �������
		CloseHandle(waitObjs[NEED_STOP]);
		CloseHandle(waitObjs[NEED_PROCESS]);
		
		//�������
		for(int i = 0; i < KThreadCount; i++)	
			CloseHandle(m_threads[i]);

		//� ���� ������
		DeleteCriticalSection(&cs);
	}

	//�������� ����� ����������
	int App::exec()
	{
		//������� ������ �������������� �������
		startThreads();
	
		//���� 30 ������
		Sleep(KAppTTL);

		//���������� ����-������
		stopper.stop();

		//���� ��������� �������
		waitThreads();
	
		//�.�. ����� ��� � �������� �� ��������, ����� �������� ��� ������������� ��
		//������� �� ������� �� ������������ �������
		while(!qRequests.empty())
		{
			delete qRequests.front();
			qRequests.pop();
		}

		return 0;
	}
private:
	//����������� �������
	HANDLE m_threads[KThreadCount + 1];		
	HANDLE m_hTimer;

	//������ �������
	void App::startThreads()
	{
		//��������� ��������� ��������
		m_threads[0] = CreateThread(NULL, 0, Collector, NULL, 0, NULL);

		//� ������ �� ������������
		for(int i  = 1; i < KThreadCount + 1; i++)
			m_threads[i] = CreateThread(NULL, 0, Worker, NULL, 0, NULL);

		cout << "All threads started\n";
	}

	//���� ���������� ������ ���� �������
	void App::waitThreads()
	{
		DWORD result = WaitForMultipleObjects(KThreadCount+1,  m_threads,  TRUE,  INFINITE);
		switch(result)
		{
		case WAIT_OBJECT_0:
			//��� ������ ������� ��������� ���� ������
			break;
		case WAIT_ABANDONED_0:
			//���� ��� ��������� ������� ����������� �� ��������� ������
			break;
		}

		cout << "All threads stopped\n";
	}
};
DWORD WINAPI Collector(LPVOID param)
{
	while(TRUE)
	{
		//����� ������
		Request* pReq = GetRequest(stopper);
		if(pReq)
		{
			//������� � ��
			EnterCriticalSection(&cs);
			
			//���� ������� ���� �����, ���������� 
			//��������� ����������� �������
			bool needNotify = qRequests.empty();

			//���� �� �������� ����� �������� � �������
			if(qRequests.size() < KReqsCountLimit)
			{
				//�������� ������ � �������
				qRequests.push(pReq);
			}
			
			cout << "Reqs wait for process: " << qRequests.size() << "\n";

			//������� �� ��
			LeaveCriticalSection(&cs);

			//���������� ������ � ����������� �������
			if(needNotify)
				SetEvent(waitObjs[NEED_PROCESS]);

		}else{
			//GetRequest ������ NULL ������ ����� ����� ��������� ���� ������
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
		//���� ���������� ������ ��������� ��������		
		result = WaitForMultipleObjects(2, waitObjs, FALSE, INFINITE);
		
		//������ �� ������� stopper � ������������� ��������� ������
		if(result - WAIT_OBJECT_0 == 0)
		{		
			cout << "Thread tid[" << tid <<"] stoped by signal\n";
			//��������� ������ ������
			break;

		//������ � ������� � ������� ��������
		}else if(result - WAIT_OBJECT_0 == 1)
		{

			//������� � ��
			EnterCriticalSection(&cs);
			//���� � ������� ���� �������, ��������� ����
			if(!qRequests.empty())
			{
				pReq = qRequests.front();
				qRequests.pop();

				cout <<"Thread tid:["<<tid<<"] processed request\n";
			}else{
				//���� ������� ����� ���������� �������, ����� ��������� 
				//������ ������� � ����� ��������,������� �� �� � 
				//���� ��������� � ����� ��������
				LeaveCriticalSection(&cs);
				ResetEvent(waitObjs[NEED_PROCESS]);				
				continue; // while(TRUE)
			}

			//������� �� ��
			LeaveCriticalSection(&cs);

			if(pReq)
			{
				//����� ��������� ������ �� ������� � ������������ ���
				ProcessRequest(pReq, stopper);

				//����� ��������� ����������� ������
				delete pReq;
				pReq = NULL;
			}			
		}
	}
	
	return 0;
}

//��������� ��������� ������ � ��������� �������� �������
int main(int argc,char** argv)
{
	App* pApp = new App;	
	int result = pApp->exec();
	delete pApp;
	system("pause");
	return(result);
}
