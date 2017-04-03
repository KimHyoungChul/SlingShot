/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/cymszb/Development/Android/dev/eclipse/workspace/SlingShot/src/com/CTC_ChinaNet/android/tm/aidl/TMServiceCISCOIMSAidl.aidl
 */
package com.CTC_ChinaNet.android.tm.aidl;
public interface TMServiceCISCOIMSAidl extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.CTC_ChinaNet.android.tm.aidl.TMServiceCISCOIMSAidl
{
private static final java.lang.String DESCRIPTOR = "com.CTC_ChinaNet.android.tm.aidl.TMServiceCISCOIMSAidl";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.CTC_ChinaNet.android.tm.aidl.TMServiceCISCOIMSAidl interface,
 * generating a proxy if needed.
 */
public static com.CTC_ChinaNet.android.tm.aidl.TMServiceCISCOIMSAidl asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.CTC_ChinaNet.android.tm.aidl.TMServiceCISCOIMSAidl))) {
return ((com.CTC_ChinaNet.android.tm.aidl.TMServiceCISCOIMSAidl)iin);
}
return new com.CTC_ChinaNet.android.tm.aidl.TMServiceCISCOIMSAidl.Stub.Proxy(obj);
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
case TRANSACTION_getIMSParameter:
{
data.enforceInterface(DESCRIPTOR);
com.CTC_ChinaNet.android.tm.aidl.CTCCISCOIMSResult _result = this.getIMSParameter();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.CTC_ChinaNet.android.tm.aidl.TMServiceCISCOIMSAidl
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
/**
	 *get IPTV token
	 * if occur error ,will be return null
     * 
     */
@Override public com.CTC_ChinaNet.android.tm.aidl.CTCCISCOIMSResult getIMSParameter() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.CTC_ChinaNet.android.tm.aidl.CTCCISCOIMSResult _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getIMSParameter, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.CTC_ChinaNet.android.tm.aidl.CTCCISCOIMSResult.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getIMSParameter = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/**
	 *get IPTV token
	 * if occur error ,will be return null
     * 
     */
public com.CTC_ChinaNet.android.tm.aidl.CTCCISCOIMSResult getIMSParameter() throws android.os.RemoteException;
}
