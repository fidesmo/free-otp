/*
 * FreeOTP
 *
 * Authors: Nathaniel McCallum <npmccallum@redhat.com>
 *
 * Copyright (C) 2014  Nathaniel McCallum, Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fedorahosted.freeotp.edit;

import org.fedorahosted.freeotp.R;
import org.fedorahosted.freeotp.InternalToken;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.squareup.picasso.Picasso;

public class EditActivity extends Activity implements TextWatcher, View.OnClickListener {
    public static final String EXTRA_TOKEN = "token";

    private InternalToken      mToken;
    private EditText           mIssuer;
    private EditText           mLabel;
    private ImageButton        mImage;
    private Button             mRestore;
    private Button             mSave;

    private String mIssuerCurrent;
    private String mIssuerDefault;
    private String mLabelCurrent;
    private String mLabelDefault;
    private Uri mImageCurrent;
    private Uri mImageDefault;
    private Uri mImageDisplay;

    private void showImage(Uri uri) {
        mImageDisplay = uri;
        onTextChanged(null, 0, 0, 0);
        Picasso.with(this)
                .load(uri)
                .placeholder(R.drawable.logo)
                .into(mImage);
    }

    private boolean imageIs(Uri uri) {
        if (uri == null)
            return mImageDisplay == null;

        return uri.equals(mImageDisplay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        mToken = (InternalToken)getIntent().getSerializableExtra(EXTRA_TOKEN);
        // Get token values.

        mIssuerCurrent = mToken.getIssuer();
        mLabelCurrent = mToken.getLabel();
        mImageCurrent = mToken.getImage();
        mToken.setIssuer(null);
        mToken.setLabel(null);
        mToken.setImage(null);
        mIssuerDefault = mToken.getIssuer();
        mLabelDefault = mToken.getLabel();
        mImageDefault = mToken.getImage();

        // Get references to widgets.
        mIssuer = (EditText) findViewById(R.id.issuer);
        mLabel = (EditText) findViewById(R.id.label);
        mImage = (ImageButton) findViewById(R.id.image);
        mRestore = (Button) findViewById(R.id.restore);
        mSave = (Button) findViewById(R.id.save);

        // Setup text changed listeners.
        mIssuer.addTextChangedListener(this);
        mLabel.addTextChangedListener(this);

        // Setup click callbacks.
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.restore).setOnClickListener(this);
        mImage.setOnClickListener(this);

        // Setup initial state.
        showImage(mImageCurrent);
        mLabel.setText(mLabelCurrent);
        mIssuer.setText(mIssuerCurrent);
        mIssuer.setSelection(mIssuer.getText().length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
            showImage(data.getData());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String label = mLabel.getText().toString();
        String issuer = mIssuer.getText().toString();
        mSave.setEnabled(!label.equals(mLabelCurrent) || !issuer.equals(mIssuerCurrent) || !imageIs(mImageCurrent));
        mRestore.setEnabled(!label.equals(mLabelDefault) || !issuer.equals(mIssuerDefault) || !imageIs(mImageDefault));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image:
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 0);
                break;

            case R.id.restore:
                mLabel.setText(mLabelDefault);
                mIssuer.setText(mIssuerDefault);
                mIssuer.setSelection(mIssuer.getText().length());
                showImage(mImageDefault);
                break;

            case R.id.save:
                mToken.setIssuer(mIssuer.getText().toString());
                mToken.setLabel(mLabel.getText().toString());
                mToken.setImage(mImageDisplay);
                Intent resultData = new Intent();
                resultData.putExtra(EXTRA_TOKEN, mToken);
                setResult(RESULT_OK, resultData);
                finish();
                break;

            case R.id.cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
