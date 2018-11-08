package ru.spbswtest;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 11.10.11
 * Time: 18:45
 * To change this template use File | Settings | File Templates.
 */
public class App
{

	public static void main(String[] args)
	{
		Params params = new Params();
		if(checkArgs(args,params))
		{
			boolean result = new Packer(params).process();
			if(result)
			{
				//
			}else{
				//
			}
		}else
		{
			printUsage();
		}
	}

	//Возхвращает true только если есть необходимость обрабатывать файлы
	private static boolean checkArgs(String[] args, Params params)
	{
		for(int i = 0;i < args.length; i++)
		{
			if(args[i].toLowerCase().equals("-h"))
				return false;

			if(args[i].toLowerCase().equals("-p") && i+1 < args.length)
			{
				if(params.action != Params.Action.Unknown)
					return false;

				params.action = Params.Action.PackAction;
				params.src = args[i+1];
				i++;
				continue;
			}

			if(args[i].toLowerCase().equals("-u") && i+1 < args.length)
			{
				if(params.action != Params.Action.Unknown)
					return false;

				params.action = Params.Action.UnpackAction;
				params.src = args[i+1];
				i++;
				continue;
			}

			if(args[i].toLowerCase().equals("-d") && i+1 < args.length)
			{
				if(params.action == Params.Action.Unknown)
					return false;

				params.dst = args[i+1];
				i++;
				continue;
			}
		}

		//Если все параметры указаны
		if(params.action != Params.Action.Unknown &&
		    !params.src.equals("") &&
			!params.dst.equals(""))
			return true;

		return false;
	}

	public static void printUsage()
	{
		System.out.println("Usage:\npack example -p C:\\dir1\\dir2\\dir3 -d c:\\packed.tar");
		System.out.println("unpack example -u c:\\packed.tar -d C:\\dir1\\dir2");
	}
}
