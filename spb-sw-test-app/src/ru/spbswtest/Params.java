package ru.spbswtest;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 11.10.11
 * Time: 19:11
 * To change this template use File | Settings | File Templates.
 */
public class Params{
	public enum Action{Unknown, PackAction, UnpackAction};
	public Action action = Action.Unknown;
	public String src;
	public String dst;
}