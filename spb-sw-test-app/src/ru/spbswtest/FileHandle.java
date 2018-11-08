package ru.spbswtest;

import java.awt.image.DirectColorModel;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 11.10.11
 * Time: 19:45
 * To change this template use File | Settings | File Templates.
 */
public class FileHandle  implements Serializable
{
	public FileHandle(String Path, boolean IsDir, long Size)
	{
		path = Path;
		isDir= IsDir;
		size = Size;
	}

	public String path;
	public boolean isDir;
	public long size;
}
