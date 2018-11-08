using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Text.RegularExpressions;

namespace DinsTestApp
{
    struct AppParams{
       public DateTime startTime;
       public DateTime stopTime;
       public FileStream inputStream;
    }


    class Program
    {
        const string KOutputFilename = "output.txt";
        const string KTimeRegExp = @"(\d+):(\d+):(\d+)";
        const int KReadBufSize = 32 * 1024 * 1024;
        
        private AppParams m_params;
        private FileStream m_outputStream;
        private byte[] m_readBuf;

        public Program(AppParams Params) 
        {
            m_params = Params;
            m_readBuf = new byte[KReadBufSize];
        }

        ~Program()
        {
            if (m_params.inputStream != null)
                m_params.inputStream.Close();
        }

        public void execute()
        {
            int count = 0;
            int r = 0;

            int lineCount = 0;
            int dateCount = 0;

            while (true)
            {
                r = m_params.inputStream.Read(m_readBuf, 0, m_readBuf.Length);

                if (r == 0)
                    break;
                
                count += r;

                //TODO Смещения
                
                for (int i = 0; i < m_readBuf.Length ; i++)
                {
                    int sepIndex = Array.IndexOf<byte>(m_readBuf, (byte)':');
                    if (sepIndex > 0 && sepIndex <= 70 && sepIndex + 5 < m_readBuf.Length && m_readBuf[sepIndex + 3] == ':')
                    { 
                        int hours = (m_readBuf[sepIndex - 2] - '0') * 10 + m_readBuf[sepIndex - 1] - '0';
                        if (hours >= 0 && hours < 24)
                        {
                            int mins = (m_readBuf[sepIndex + 1] - '0') * 10 + m_readBuf[sepIndex + 2] - '0';
                            if (mins >= 0 && mins < 60)
                            {
                                int secs = (m_readBuf[sepIndex + 4] - '0') * 10 + m_readBuf[sepIndex + 1] - '0';
                                if (secs >= 0 && secs < 60)
                                {
                                    //нашли дату
                                    dateCount++;
                                    
                                }
                            }
                        }
                    }


                    if (m_readBuf[i] == 0xA || i < m_readBuf.Length - 1 && m_readBuf[i] == 0xD && m_readBuf[i + 1] == 0xA)
                        lineCount++;
                }
                
                //                
                //Console.WriteLine("{0} mb readed", (count/(1024*1024)));
            }
            Console.WriteLine("lineCount {0}", lineCount);
            Console.WriteLine("dateCount {0}", dateCount);

            //Match m5 = Regex.Match(str, KTimeRegExp);
        }

        static bool handleAppArgs(ref string[] args, ref AppParams appParams)
        {
            //Кол-во аргументов
            if (args != null && args.Length != 3)
            {
                Console.WriteLine("app.exe path_to_file start_time end_time");
                return false;
            }

            //Проверяем время начала и конца
            try
            {
                appParams.startTime = DateTime.Parse(args[1]);
            }
            catch (Exception ex)
            { 
                Console.WriteLine("Wrong start time");
                return false;
            }

            try
            {
                appParams.stopTime = DateTime.Parse(args[2]);
            }
            catch (Exception ex)
            { 
                Console.WriteLine("Wrong stop time");
                return false;
            }
            
            if (appParams.stopTime < appParams.startTime)
            {
                Console.WriteLine("Stop time should be more that start time");
                return false;
            }

            //Проверка сущ файла и возможности его чтения
            string filePath = args[0];
            if (File.Exists(filePath))
            {
                try
                {
                    appParams.inputStream = File.OpenRead(filePath);
                }
                catch (Exception ex) 
                {
                    Console.WriteLine("Can't open file, " + ex.Message);
                    return false;
                }

                if (!appParams.inputStream.CanRead)
                {
                    appParams.inputStream.Close();
                    Console.WriteLine("Can't read file");
                    return false;
                }
            }
            else {
                Console.WriteLine("File don't exists");
                return false;
            }

            return true;
        }

        static void Main(string[] args)
        {
            AppParams appParams = new AppParams();
            if(handleAppArgs(ref args,ref appParams))
            {
                Program p = new Program(appParams);
                p.execute();
            }
        }
    }
}
