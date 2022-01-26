# kimai-nfc

KimaiNFC is a companion app for the [Kimai Time-Tracker](https://www.kimai.org/de/) which lets you clock in and out by scanning an NFC tag with your smartphone

> Kimai has an app listed on their website, [Kimai Mobile](https://www.kimai.org/store/cloudrizon-kimai-mobile-app-android.html) but i'm not sure if it supports NFC tags

> Kimai also has an official plugin, [Kimai Kiosk](https://www.kimai.org/store/kiosk-barcode-bundle.html) which seems to have the same goals, but with a terminal instead of your own smartphone

## Preparation

### acquiring nfc tags
You can use any NFC Tag you want, just make sure it supports the NDEF standard

### preparing the NFC Tags
In order to recognize the NFC tag there needs to be some metadata present which you need to write to the NFC tag manually

> [issue#1](https://github.com/danielr1996/kimai-nfc/issues/1) addresses this, so in a future version this might be possible directly from the app

I used the [NFC Tools](https://play.google.com/store/apps/details?id=com.wakdev.wdnfc&hl=de&gl=US) app, but you can use any app you want to write the tags. 

Write a dataset of type URL/URI with the schema `kimai://` and any value you want.

Now your NFC Tag is ready to be recognized by the app.

##
To use the API you need to create an api password which is different from your password used to login on the website. 
To set the password login on the website, click on your profile picture in the top right and select "My Profile", on the right select "API" and enter your api password. 

Now open the app and fill in the fields. If you just have one project and activity you can put in 1 for each.

