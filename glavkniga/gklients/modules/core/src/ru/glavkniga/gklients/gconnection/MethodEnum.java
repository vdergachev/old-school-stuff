/*
 * Copyright (c) 2015 gklients
 */

package ru.glavkniga.gklients.gconnection;

/**
 * Created by LysovIA on 09.12.2015.
 */
public enum MethodEnum {
    login,
    logout,
    setUsers,
    setRegkeys,
    setSchedule,
    setRizData,
    countUsers,
    isUser,

    isRegkey,
    countRegkeys,

    countTests,
    countTestEmails,
    countTestMarks,

    getTests,
    getTestEmails,
    getTestMarks,
    isTestEmail,

    getUser,
    getUsersList,
    getUserData,
    getUserServices,
    getUserServiceData,
    deleteUserData,


    setUser;


}
