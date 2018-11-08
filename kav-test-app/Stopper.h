#pragma once
#include <Windows.h>

class Stopper
{
public:
	Stopper::Stopper() : m_hEvent(0){}
private:
	HANDLE m_hEvent;
public: 
	
	void Stopper::setEventHandle(HANDLE h)
	{
		m_hEvent = h;
	}

	//Останавливаем 
	void Stopper::stop()
	{
		SetEvent(m_hEvent);
	}

	
	bool Stopper::isStopped()
	{
		DWORD result = WaitForSingleObject(m_hEvent, 0);
		if(!result)
			return true;

		return false;
	}
};