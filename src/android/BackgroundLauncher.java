// Copyright (c) 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package be.betalife.cordova.plugin.epsonposprinter;

import android.app.Activity;
import android.os.Bundle;

public class BackgroundLauncher extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Should forward intent action / extras / flags.
        BackgroundActivity.launchForeground(this, true);
        finish();
    }
}
