package ru.spbswtest;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 11.10.11
 * Time: 18:52
 * To change this template use File | Settings | File Templates.
 */


public class Packer
{
	private Params m_params;

	public Packer(Params params){
		m_params = params;
	}

	public boolean process()
	{
		switch (m_params.action)
		{
			case PackAction:
				//Упаковываем
				pack(m_params.src, m_params.dst);
				break;

			case UnpackAction:
				//Распаковываем
				unpack(m_params.src, m_params.dst);
				break;

			case Unknown:
				break;

		}
		return false;
	}

	private boolean pack(String srcDir, String dstFile)
	{
		//Получить список файлов и директорий в виде списка из фс
		final Collection<FileHandle> allFiles = new ArrayList<FileHandle>();
        getChildrens(new File(srcDir), allFiles);

		//Пишем заголовок в файл
		FileOutputStream fos = null;
		try
		{
		    fos   = new FileOutputStream(dstFile);
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(allFiles);


		} catch(IOException e)
		{
		    System.err.println("Can't write to tar-file");
			return false;
		}

		//Пишем содержимое файлов в тар-файл
		for (FileHandle fh : allFiles){
			if(fh.isDir)
				continue;

			FileInputStream from = null;
			try {
				from = new FileInputStream(new File(fh.path));

				byte[] buffer = new byte[4096*4];
				int bytesRead;

				while ((bytesRead = from.read(buffer)) != -1)
					fos.write(buffer, 0, bytesRead);

				from.close();

			}catch (Exception ex)
			{
				System.err.println("Can't write to tar-file");
			}
		}

		try
		{
			fos.close();
		} catch (IOException e)
		{
			 System.err.println("Can't close tar-file");
		}
		return false;
	}

	private boolean unpack(String srcFile, String dstDir)
	{
		//Получить список файлов и директорий в виде списка из архива
		ArrayList<FileHandle> allFiles = null;
		try{
			FileInputStream fis = new FileInputStream(srcFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			allFiles = (ArrayList)ois.readObject();

			ois.close();
		}
		catch(IOException e)
		{
			System.out.println("Can't read tar-file header");
			return false;
		}
		catch (Exception ex)
		{
			System.out.println("Can't read tar-file header");
			return false;
		}

		if(allFiles == null)
			return false;

		for(FileHandle fh : allFiles)
		{
			if(fh.isDir)
			{
				//Создаем папку
			}else{
				//Читаем известный нам размер байт и сохраняем прочитаное в файл
			}
		}
		return false;
	}


	private void getChildrens(File file, Collection<FileHandle> allFiles) {
		final File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				allFiles.add(new FileHandle(child.getPath(),child.isDirectory(), child.length()));
				getChildrens(child, allFiles);
			}
		}
	}
}
