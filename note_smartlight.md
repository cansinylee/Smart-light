SMARTLIGHT SYSTEM

1. Set Up the Server (VPS) to Store and manage the Light Status.
2. Create an android app that can connect to VPS for sending request to change the light status.
3. Program the Pico set to connect it to VPS to get status of the light and responds accordingly.
4. Test and Run the System

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


1. Set Up the Server (VPS) to Store and manage the Light Status.
### **Step 1: Connect to Azure VM**


### **Step 2: Install Node.js on the Azure VM**
Ensure that Node.js is installed on the VM.

1. **Update the package list**:
   ```bash
   sudo apt update
   ```

2. **Install Node.js and npm**:
   ```bash
   sudo apt install -y nodejs npm
   ```

3. **Verify the installation**:
   ```bash
   node -v
   npm -v
   ```

---

### **Step 3: Set Up the Project on the VM**
1. **Create a project directory**:
   ```bash
   mkdir light-api
   cd light-api
   ```

2. **Initialize the project**:
   ```bash
   npm init -y
   ```

3. **Install Express.js**:
   ```bash
   npm install express
   ```

4. **Create the `app.js` file**:
   ```bash
   nano app.js
   ```

5. **Here is the API code for `app.js`** (replace `localhost` with `0.0.0.0` to listen on all interfaces):
   ```javascript
   const express = require('express');
   const app = express();
   const port = 3000;

   let lightState = false;

   app.get('/set-light-on', (req, res) => {
       lightState = true;
       res.send({ status: "Light is ON" });
   });

   app.get('/set-light-off', (req, res) => {
       lightState = false;
       res.send({ status: "Light is OFF" });
   });

   app.get('/get-light-status', (req, res) => {
       res.send({ light_on: lightState });
   });

   app.listen(port, '0.0.0.0', () => {
       console.log(`Server running on port ${port}`);
   });
   ```

6. **Save and exit**: 
  

---

### **Step 4: Open Port 3000 in Azure**
1. Log in to the Azure portal.
2. Navigate to your VM's **Networking** settings.
3. Add a new **Inbound Port Rule**:
   - **Destination Port**: `3000`
   - **Protocol**: TCP
   - **Action**: Allow
   - **Priority**: Set appropriately (e.g., `1000`).
   - **Name**: `Allow-Port-3000`


            General Rule Configuration for HTTP (Port 3000)**:
            | **Field**                 | **Value**                                                |
            |---------------------------|----------------------------------------------------------|
            | **Source**                | `Any`                                                   |
            | **Source IP addresses/CIDR ranges** | `*` (Allow all IP addresses) or specify IP in CIDR format if we want restricted access (e.g., `203.0.113.0/24`). |
            | **Source Port Ranges**    | `*` (Any)                                               |
            | **Destination**           | `Any`                                                   |
            | **Destination Port Ranges** | `3000` (The port our API server is running on)       |
            | **Protocol**              | `TCP` (Because HTTP uses TCP)                           |
            | **Action**                | `Allow`                                                 |
            | **Priority**              | A low value, e.g., `100` (higher priority than other rules). |
            | **Name**                  | A meaningful name, e.g., `Allow_HTTP_API`               |


---

### **Step 5: Start the Server on the VM**
1. Start the server:
   ```bash
   node app.js
   ```
2. We might see:
   ```
   Server running on port 3000
   ```

---

### **Step 6: Access the API Endpoints**
With VM's public IP (`20.93.3.161`), we can now access the API from anywhere.

1. **Turn the light on**:
   - URL: `http://20.93.3.161:3000/set-light-on`

2. **Turn the light off**:
   - URL: `http://20.93.3.161:3000/set-light-off`

3. **Check the light status**:
   - URL: `http://20.93.3.161:3000/get-light-status`

---

### **Step 7: Keep the Server Running**
To ensure the server runs even after we disconnect from the SSH session, use **`pm2`**:

#### **a. Install `pm2`**
```bash
sudo npm install -g pm2
pm2 start app.js
pm2 save
pm2 startup
```

---

### **Step 8: Test and Use the API**
Use `curl`, or browser to test the endpoints.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------






2. Create an android app that can connect to VPS for sending request to change the light status.


    Design the UI with 2 buttons (on and off) 
    
**here is the xml file code:**
```java
    <?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:context=".MainActivity"
    android:background="@drawable/background"
    android:id="@+id/root_layout">


    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:fitsSystemWindows="true">

    </com.google.android.material.appbar.AppBarLayout>

    <Button
        android:id="@+id/button_on"
        android:layout_width="95dp"
        android:layout_height="80dp"
        android:layout_marginStart="160dp"
        android:layout_marginTop="400dp"
        android:foreground="@drawable/button_on"
        android:onClick="turnLightOn"       //call the onclick funtion in java file.
        android:text="On"
        tools:ignore="HardcodedText" />

    <Button
        android:id="@+id/button_off"
        android:layout_width="91dp"
        android:layout_height="80dp"
        android:layout_marginStart="160dp"
        android:layout_marginTop="500dp"
        android:foreground="@drawable/button_off"
        android:onClick="turnLightOff"       //call the click funtion in java file.
        android:text="Off"
        tools:ignore="HardcodedText" />

    <EditText
        android:id="@+id/editTextText"
        android:layout_width="237dp"
        android:layout_height="48dp"
        android:layout_marginStart="90dp"
        android:layout_marginTop="100dp"
        android:background="@null"
        android:ems="10"
        android:inputType="text"
        android:text="Smartlight"
        android:textAlignment="center"
        android:textColor="@color/white"
        android:textSize="34sp"
        android:textStyle="bold|italic" />


</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

**here is the Java file code:**
```java

package com.example.smartlight_final;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.smartlight_final.databinding.ActivityMainBinding;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private CoordinatorLayout rootLayout;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootLayout = findViewById(R.id.root_layout);
    }

    public void turnLightOn(View view) {
        sendRequest("http://20.93.3.161:3001/set-light-on");
        changeBackground(true);
    }

    public void turnLightOff(View view) {
        sendRequest("http://20.93.3.161:3001/set-light-off");
        changeBackground(false);
    }

    private void sendRequest(String urlString) {
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode(); // Trigger the request
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void changeBackground(boolean isLightOn) {     //change background when click the button
        if (isLightOn) {

            rootLayout.setBackgroundResource(R.drawable.background);
        } else {

            rootLayout.setBackgroundResource(R.drawable.background_off);
        }
    }
}
```
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------







3. Program the Pico set to connect it to VPS to get status of the light and responds accordingly.


    3.1 **Set Up Wi-Fi and HTTP Communication**:
  
   - Use the `urequests` library in MicroPython to send HTTP requests from the Pico to the server.

    3.2 **Write Code for Polling Light Status**:
   - The Raspberry Pi Pico will use **polling** to check the light’s current status by calling the `/get-light-status` endpoint.
   - If it receives a "true" status, it turns the light on; if "false," it turns the light off.

 
   
   ```python
   import urequests
   import time
   from machine import Pin
   import network

   # Set up the light pin on the Raspberry Pi Pico
   led = Pin(1, Pin.OUT)

   # Connect to Wi-Fi
   wlan = network.WLAN(network.STA_IF)
   wlan.active(True)
   wlan.connect('Wi-Fi_SSID', 'Wi-Fi_PASSWORD')

   while not wlan.isconnected():
       pass

   # Function to check light status from server
   def check_light_status():
       response = urequests.get("http://20.93.3.161:3000/get-light-status")
       data = response.json()
       response.close()
       if data["light_on"]:
           led.on()
       else:
           led.off()

   # Polling loop
   while True:
       check_light_status()
       time.sleep(5)  # Poll every 5 seconds
   ```

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------






4. Test and Run the System

    4.1 **Check Server and Connections**:
   - Ensure that both the Raspberry Pi Pico and the Android app can reach the server over the network.

    4.2 **Verify Control Functionality**:
   - Test turning the light on and off from the Android app and verify that the Raspberry Pi Pico responds accordingly.

