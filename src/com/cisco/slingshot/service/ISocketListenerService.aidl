package com.cisco.slingshot.service;

interface ISocketListenerService
{
    String getIP();
    String getPort();
    String getStatus();
    void reStart(String port);
}