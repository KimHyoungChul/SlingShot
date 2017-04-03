package com.cisco.slingshot.net.sip;

import java.text.ParseException;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.sip.*;

public class SipManagerExtd {

    private Context mContext;
    private SipManager mSipmanager;
    
	public SipManagerExtd(Context context) {
		 mSipmanager = SipManager.newInstance(context);
		 mContext = context;
		// TODO Auto-generated constructor stub
	}


    /**
     * Returns true if the system supports camera API.
     */	
    public static boolean isCameraSupported(Context context) {
    
    	return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    /**
     * Closes the specified profile to not make/receive calls. All the resources
     * that were allocated to the profile are also released.
     *
     * @param localProfileUri the URI of the profile to close
     * @throws SipException if calling the SIP service results in an error
     */
    public void close(String localProfileUri) throws SipException {
    	
    	mSipmanager.close(localProfileUri);
    	return;
    }

    /**
     * Creates a {@link SipSession} with the specified profile. Use other
     * methods, if applicable, instead of interacting with {@link SipSession}
     * directly.
     *
     * @param localProfile the SIP profile the session is associated with
     * @param listener to listen to SIP session events
     */
    public SipSession createSipSession(SipProfile localProfile,
            SipSession.Listener listener) throws SipException{
    	
    	return mSipmanager.createSipSession(localProfile, listener);
    }

    /**
     * Gets the list of profiles hosted by the SIP service. The user information
     * (username, password and display name) are crossed out.
     * @hide
     */
    public SipProfile[] getListOfProfiles() {
    	
    	return mSipmanager.getListOfProfiles();
    }

    /**
     * Gets the {@link SipSession} that handles the incoming call. For audio
     * calls, consider to use {@link SipConfCall} to handle the incoming call.
     * See {@link #takeConfCall}. Note that the method may be called only once
     * for the same intent. For subsequent calls on the same intent, the method
     * returns null.
     *
     * @param incomingCallIntent the incoming call broadcast intent
     * @return the session object that handles the incoming call
     */
    public SipSession getSessionFor(Intent incomingCallIntent)
    		throws SipException{
    	return mSipmanager.getSessionFor(incomingCallIntent);
    }
    
    /**
     * Checks if the specified profile is opened in the SIP service for
     * making and/or receiving calls.
     *
     * @param localProfileUri the URI of the profile in question
     * @return true if the profile is enabled to receive calls
     * @throws SipException if calling the SIP service results in an error
     */    
    public boolean isOpened(String localProfileUri) 
    		throws SipException {
    	
    	return mSipmanager.isOpened(localProfileUri);
    }

    /**
     * Checks if the SIP service has successfully registered the profile to the
     * SIP provider (specified in the profile) for receiving calls. Returning
     * true from this method also implies the profile is opened
     * ({@link #isOpened}).
     *
     * @param localProfileUri the URI of the profile in question
     * @return true if the profile is registered to the SIP provider; false if
     *        the profile has not been opened in the SIP service or the SIP
     *        service has not yet successfully registered the profile to the SIP
     *        provider
     * @throws SipException if calling the SIP service results in an error
     */
    public boolean isRegistered(String localProfileUri) throws SipException{
    	
    	return mSipmanager.isRegistered(localProfileUri);
    }
    
    /**
     * Opens the profile for making generic SIP calls. The caller may make subsequent calls
     * through {@link #makeAudioCall} or  {@link #makeConfCall}. If one also wants to receive
     *  calls on the profile, use
     * {@link #open(SipProfile, PendingIntent, SipRegistrationListener)}
     * instead.
     *
     * @param localProfile the SIP profile to make calls from
     * @throws SipException if the profile contains incorrect settings or
     *      calling the SIP service results in an error
     */
    public void open(SipProfile localProfile) throws SipException {
    	
    	mSipmanager.open(localProfile);
    	return;
    }

    /**
     * Opens the profile for making calls and/or receiving generic SIP calls. The caller may
     * make subsequent calls through {@link #makeAudioCall} or {@link #makeConfCall}. If the
     * auto-registration option is enabled in the profile, the SIP service
     * will register the profile to the corresponding SIP provider periodically
     * in order to receive calls from the provider. When the SIP service
     * receives a new call, it will send out an intent with the provided action
     * string. The intent contains a call ID extra and an offer session
     * description string extra. Use {@link #getCallId} and
     * {@link #getOfferSessionDescription} to retrieve those extras.
     *
     * @param localProfile the SIP profile to receive incoming calls for
     * @param incomingCallPendingIntent When an incoming call is received, the
     *      SIP service will call
     *      {@link PendingIntent#send(Context, int, Intent)} to send back the
     *      intent to the caller with {@link #INCOMING_CALL_RESULT_CODE} as the
     *      result code and the intent to fill in the call ID and session
     *      description information. It cannot be null.
     * @param listener to listen to registration events; can be null
     * @see #getCallId
     * @see #getOfferSessionDescription
     * @see #takeAudioCall
     * @throws NullPointerException if {@code incomingCallPendingIntent} is null
     * @throws SipException if the profile contains incorrect settings or
     *      calling the SIP service results in an error
     * @see #isIncomingCallIntent
     * @see #getCallId
     * @see #getOfferSessionDescription
     */
    public void open(SipProfile localProfile,
            PendingIntent incomingCallPendingIntent,
            SipRegistrationListener listener) throws SipException {
    	
    	mSipmanager.open(localProfile, incomingCallPendingIntent, listener);
    	return;
    }

    /**
     * Manually registers the profile to the corresponding SIP provider for
     * receiving calls.
     * {@link #open(SipProfile, PendingIntent, SipRegistrationListener)} is
     * still needed to be called at least once in order for the SIP service to
     * notify the caller with the {@link android.app.PendingIntent} when an incoming call is
     * received.
     *
     * @param localProfile the SIP profile to register with
     * @param expiryTime registration expiration time (in seconds)
     * @param listener to listen to the registration events
     * @throws SipException if calling the SIP service results in an error
     */
    public void register(SipProfile localProfile, int expiryTime,
            SipRegistrationListener listener) throws SipException {
    	
    	mSipmanager.register(localProfile, expiryTime, listener);
    	return;
    }

    /**
     * Manually unregisters the profile from the corresponding SIP provider for
     * stop receiving further calls. This may interference with the auto
     * registration process in the SIP service if the auto-registration option
     * in the profile is enabled.
     *
     * @param localProfile the SIP profile to register with
     * @param listener to listen to the registration events
     * @throws SipException if calling the SIP service results in an error
     */
    public void unregister(SipProfile localProfile,
            SipRegistrationListener listener) throws SipException {
    	
    	mSipmanager.unregister(localProfile, listener);
    	return;
    }

    /**
     * Sets the listener to listen to registration events. No effect if the
     * profile has not been opened to receive calls (see
     * {@link #open(SipProfile, PendingIntent, SipRegistrationListener)}).
     *
     * @param localProfileUri the URI of the profile
     * @param listener to listen to registration events; can be null
     * @throws SipException if calling the SIP service results in an error
     */
    public void setRegistrationListener(String localProfileUri,
            SipRegistrationListener listener) throws SipException {
    	
    	mSipmanager.setRegistrationListener(localProfileUri, listener);
    	return;
    }

    /**
     * Creates a {@link SipAudioCall} to make an audio call. The attempt will be
     * timed out if the call is not established within {@code timeout} seconds
     * and
     * {@link SipAudioCall.Listener#onError onError(SipAudioCall, SipErrorCode.TIME_OUT, String)}
     * will be called.
     *
     * @param localProfileUri URI of the SIP profile to make the call from
     * @param peerProfileUri URI of the SIP profile to make the call to
     * @param listener to listen to the call events from {@link SipAudioCall};
     *      can be null
     * @param timeout the timeout value in seconds. Default value (defined by
     *        SIP protocol) is used if {@code timeout} is zero or negative.
     * @return a {@link SipAudioCall} object
     * @throws SipException if calling the SIP service results in an error or
     *      VOIP API is not supported by the device
     * @see SipAudioCall.Listener#onError
     * @see #isVoipSupported
     */
    public SipAudioCall makeAudioCall(String localProfileUri,
            String peerProfileUri, SipAudioCall.Listener listener, int timeout)
            throws SipException {
    	
    	return mSipmanager.makeAudioCall(localProfileUri, peerProfileUri, listener, timeout);
    }

    /**
     * Creates a {@link SipAudioCall} to make a call. The attempt will be timed
     * out if the call is not established within {@code timeout} seconds and
     * {@link SipAudioCall.Listener#onError onError(SipAudioCall, SipErrorCode.TIME_OUT, String)}
     * will be called.
     *
     * @param localProfile the SIP profile to make the call from
     * @param peerProfile the SIP profile to make the call to
     * @param listener to listen to the call events from {@link SipAudioCall};
     *      can be null
     * @param timeout the timeout value in seconds. Default value (defined by
     *        SIP protocol) is used if {@code timeout} is zero or negative.
     * @return a {@link SipAudioCall} object
     * @throws SipException if calling the SIP service results in an error or
     *      VOIP API is not supported by the device
     * @see SipAudioCall.Listener#onError
     * @see #isVoipSupported
     */
    public SipAudioCall makeAudioCall(SipProfile localProfile,
            SipProfile peerProfile, SipAudioCall.Listener listener, int timeout)
            throws SipException {
    	
    	return mSipmanager.makeAudioCall(localProfile, peerProfile, listener, timeout);
    }

    /**
     * Creates a {@link SipAudioCall} to take an incoming call. Before the call
     * is returned, the listener will receive a
     * {@link SipAudioCall.Listener#onRinging}
     * callback.
     *
     * @param incomingCallIntent the incoming call broadcast intent
     * @param listener to listen to the call events from {@link SipAudioCall};
     *      can be null
     * @return a {@link SipAudioCall} object
     * @throws SipException if calling the SIP service results in an error
     */
    public SipAudioCall takeAudioCall(Intent incomingCallIntent,
            SipAudioCall.Listener listener) throws SipException {
    	
    	return mSipmanager.takeAudioCall(incomingCallIntent, listener);
    }

    /**
     * Creates a {@link SipConfCall} to make a call. The attempt will be timed
     * out if the call is not established within {@code timeout} seconds and
     * {@link SipConfCall.Listener#onError onError(SipConfCall, SipErrorCode.TIME_OUT, String)}
     * will be called.
     *
     * @param localProfile the SIP profile to make the call from
     * @param peerProfile the SIP profile to make the call to
     * @param listener to listen to the call events from {@link SipConfCall};
     *      can be null
     * @param timeout the timeout value in seconds. Default value (defined by
     *        SIP protocol) is used if {@code timeout} is zero or negative.
     * @return a {@link SipConfCall} object
     * @throws SipException if calling the SIP service results in an error or
     *      VOIP API is not supported by the device
     * @see SipAudioCall.Listener#onError
     * @see #isVoipSupported
     */
	public SipConfCall makeConfCall (SipProfile localProfile, SipProfile peerProfile,
			SipConfCall.Listener listener, int timeout) throws SipException {

		if (!SipManager.isVoipSupported(mContext)) {
		    throw new SipException("VOIP API is not supported");
		}
		
		if (isCameraSupported(mContext)) {
			throw new SipException("Camera API is not supported");
		}
		
		SipConfCall call = new SipConfCall(mContext, localProfile);
		call.setListener(listener);
		SipSession s = createSipSession(localProfile, null);
		call.makeConfCall(peerProfile, s, timeout);
		return call;
	}

    /**
     * Creates a {@link SipConfCall} to make an audio call. The attempt will be
     * timed out if the call is not established within {@code timeout} seconds
     * and
     * {@link SipConfCall.Listener#onError onError(SipConfCall, SipErrorCode.TIME_OUT, String)}
     * will be called.
     *
     * @param localProfileUri URI of the SIP profile to make the call from
     * @param peerProfileUri URI of the SIP profile to make the call to
     * @param listener to listen to the call events from {@link SipConfCall};
     *      can be null
     * @param timeout the timeout value in seconds. Default value (defined by
     *        SIP protocol) is used if {@code timeout} is zero or negative.
     * @return a {@link SipConfCall} object
     * @throws SipException if calling the SIP service results in an error or
     *      VOIP API is not supported by the device
     * @see SipAudioCall.Listener#onError
     * @see #isVoipSupported
     */
	public SipConfCall makeConfCall (String localProfileUri, String peerProfileUri,
			SipConfCall.Listener listener, int timeout) throws SipException {
		
        if (!SipManager.isVoipSupported(mContext)) {
            throw new SipException("VOIP API is not supported");
        }
  
		if (isCameraSupported(mContext)) {
			throw new SipException("Camera API is not supported");
		}
		
        try {
            return makeConfCall(
                    new SipProfile.Builder(localProfileUri).build(),
                    new SipProfile.Builder(peerProfileUri).build(), listener,
                    timeout);
        } catch (ParseException e) {
            throw new SipException("build SipProfile", e);
        }
	}

    /**
     * Creates a {@link SipConfCall} to take an incoming call. Before the call
     * is returned, the listener will receive a
     * {@link SipConfCall.Listener#onRinging}
     * callback.
     *
     * @param incomingCallIntent the incoming call broadcast intent
     * @param listener to listen to the call events from {@link SipConfCall};
     *      can be null
     * @return a {@link SipConfCall} object
     * @throws SipException if calling the SIP service results in an error
     */
	public SipConfCall takeConfCall (Intent incomingCallIntent, SipConfCall.Listener listener)
	throws SipException {
        if (incomingCallIntent == null) {
            throw new SipException("Cannot retrieve session with null intent");
        }

        String callId = SipManager.getCallId(incomingCallIntent);
        if (callId == null) {
            throw new SipException("Call ID missing in incoming call intent");
        }

        String offerSd = SipManager.getOfferSessionDescription(incomingCallIntent);
        if (offerSd == null) {
            throw new SipException("Session description missing in incoming "
                    + "call intent");
        }

        try {
            SipSession session = getSessionFor(incomingCallIntent);
            if (session == null) {
                throw new SipException("No pending session for the call");
            }
            SipConfCall call = new SipConfCall(
                    mContext, session.getLocalProfile());
            call.attachCall(session, offerSd);
            call.setListener(listener);
            return call;
        } catch (Throwable t) {
            throw new SipException("takeConfCall()", t);
        }
	}

}
