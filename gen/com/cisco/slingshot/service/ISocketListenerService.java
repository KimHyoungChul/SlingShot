/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/cymszb/Development/Android/dev/eclipse/workspace/SlingShot/src/com/cisco/slingshot/service/ISocketListenerService.aidl
 */
package com.cisco.slingshot.service;
public interface ISocketListenerService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.cisco.slingshot.service.ISocketListenerService
{
private static final java.lang.String DESCRIPTOR = "com.cisco.slingshot.service.ISocketListenerService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.cisco.slingshot.service.ISocketListenerService interface,
 * generating a proxy if needed.
 */
public static com.cisco.slingshot.service.ISocketListenerService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.cisco.slingshot.service.ISocketListenerService))) {
return ((com.cisco.slingshot.service.ISocketListenerService)iin);
}
return new com.cisco.slingshot.service.ISocketListenerService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getIP:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getIP();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getPort:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getPort();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getStatus:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getStatus();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_reStart:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.reStart(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.cisco.slingshot.service.ISocketListenerService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public java.lang.String getIP() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getIP, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getPort() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPort, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void reStart(java.lang.String port) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(port);
mRemote.transact(Stub.TRANSACTION_reStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getIP = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getPort = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_reStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public java.lang.String getIP() throws android.os.RemoteException;
public java.lang.String getPort() throws android.os.RemoteException;
public java.lang.String getStatus() throws android.os.RemoteException;
public void reStart(java.lang.String port) throws android.os.RemoteException;
}
