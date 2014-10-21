/*
 * FreeOTP
 *
 * Authors: Nathaniel McCallum <npmccallum@redhat.com>
 *
 * Copyright (C) 2013  Nathaniel McCallum, Red Hat
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

/*
 * Portions Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fedorahosted.freeotp;

import java.io.IOException;

import org.fedorahosted.freeotp.add.ScanActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.WindowManager.LayoutParams;

public abstract class MainActivity extends Activity implements OnMenuItemClickListener {
    protected abstract boolean isInternal();
    protected abstract Class getAddActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Don't permit screenshots since these might contain OTP codes.
        getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_scan).setVisible(ScanActivity.haveCamera());
        menu.findItem(R.id.action_scan).setOnMenuItemClickListener(this);
        menu.findItem(R.id.action_add).setOnMenuItemClickListener(this);
        menu.findItem(R.id.action_about).setOnMenuItemClickListener(this);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
        case R.id.action_scan:
            i = new Intent(this, ScanActivity.class);
            i.putExtra(ScanActivity.EXTRA_IS_INTERNAL, isInternal());
            startActivity(i);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            return true;

        case R.id.action_add:
            startActivity(new Intent(this, getAddActivity()));
            return true;

        case R.id.action_about:
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return false;
    }

}
