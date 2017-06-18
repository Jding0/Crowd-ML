package osu.crowd_ml.firebase;

/*
    Copyright 2016 Crowd-ML team


    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License
*/

import android.os.Bundle;
import android.util.Log;

import osu.crowd_ml.ILoginView;

public class SetupPresenter implements ILoginPresenter, IFirebaseInteractor.AuthStateListener,
        IFirebaseInteractor.OnCreateUserListener, IFirebaseInteractor.OnSigninUserListener {

    private ILoginView mView;
    private boolean mReceivedUserInfo = false;

    public SetupPresenter(ILoginView view){
        mView = view;
    }

    @Override
    public void onCreate(){
        // Pass
    }

    @Override
    public void onStart() {
        if (mView != null){
            mView.showCreatingUser();
            FirebaseInteractor.getInstance().addAuthStateListener(this);
            FirebaseInteractor.getInstance().createUserAccount(this);
        }
    }

    @Override
    public void onStop() {
        FirebaseInteractor.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onDestroy() {
        mView = null;

        // TODO: REMOVE - Only for testing
        FirebaseInteractor.getInstance().destroyFirebaseListener();
    }

    /**
     * This is the callback that gets handled when the single, distributed firebase auth listener
     * from FirebaseInteractor is passively signaled of a sign in. This gets triggered multiple
     * times when a user signs in and is a pain to deal with.
     */
    @Override
    public void onSignIn(Bundle userInfo) {
        if (!mReceivedUserInfo && mView != null){
            mView.addUserInfoToPreferences(userInfo);
        }
        Log.d("SetupPresenter", "User Logged in.");
    }

    @Override
    public void onSignOut() {
        if (mView != null){
            mView.showUserOffline();
        }
    }

    @Override
    public void onCreateUserSuccess() {
        if (mView != null){
            mView.showUserSigninSuccess();
        }
    }

    @Override
    public void onCreateUserFailure() {
        if (mView != null){
            mView.showErrorCreatingUser();
        }
    }

    @Override public void onUserAlreadyExists(){
        if (mView != null){
            mView.showUserSigningIn();
            FirebaseInteractor.getInstance().signInUser(this);
        }
    }

    /**
     * This is the callback that gets handled when the application actively attempts to perform a
     * user sign in. We handle the sign in functionality in this callback.
     */
    @Override
    public void onSigninUserSuccess() {
        if (mView != null){
            mView.showUserSigninSuccess();
        }
    }

    @Override
    public void onSigninUserFailure() {
        if (mView != null){
            mView.showErrorSigningin();
        }
    }
}
