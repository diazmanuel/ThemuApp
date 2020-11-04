package com.gloves.themu.classes

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import java.nio.ByteBuffer
import java.util.*


class Ble(val context: Context){

    val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var deviceName: String? = null
    private val SCAN_PERIOD: Long = 10000
    private val TAG = "BLE"
    private val TAG2 = "NOTIFY"

    private var bluetoothGatt: BluetoothGatt? = null
    var connected = false
    var fingers = intArrayOf(0, 0, 0, 0, 0)
    var quaternion = floatArrayOf(0f, 0f, 0f, 0f)
    var refVector = floatArrayOf(1f, 0f, 0f)
    var newVector = floatArrayOf(0f, 0f, 0f)
    var oldVector = floatArrayOf(0f, 0f, 0f)
    var difVector = floatArrayOf(0f, 0f, 0f)
    private var mcuFlag: Boolean = false
    private var flexFlag: Boolean = false
    private var led = 0
    private var process : ((IntArray, FloatArray) -> Int)? = null

    private val descQueue: Queue<Pair<Int,Boolean>> = LinkedList()

    fun start(){

        val handler = Handler()
        val filters = mutableListOf<ScanFilter>()
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

        filters.add(
            ScanFilter.Builder().setServiceUuid(ParcelUuid(expandUuid(uuidSrvFlex))).build()
        )
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(expandUuid(uuidSrvMCU))).build())
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(expandUuid(uuidSrvRst))).build())
        filters.add(
            ScanFilter.Builder().setServiceUuid(ParcelUuid(expandUuid(uuidSrvBatery))).build()
        )
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(expandUuid(uuidSrvLed))).build())

        handler.postDelayed({
            adapter.bluetoothLeScanner.stopScan(scanCallback)
        }, SCAN_PERIOD)
        adapter.bluetoothLeScanner.startScan(filters, settings, scanCallback)
        Log.i(TAG, "START SCANNING")
    }
    fun close(){
        bluetoothGatt?.let {
            Log.i(TAG, "DISCONNECT")
            it.disconnect()
            it.close()
            null
        }
    }
    fun notify(enable: Boolean){
        descQueue.add(Pair(0,enable))
        descQueue.add(Pair(1,enable))
        writeNextDescriptor()
    }
    fun writeNextDescriptor(){
        val characteristic: BluetoothGattCharacteristic?
        val descriptor : BluetoothGattDescriptor?
        descQueue.poll()?.let {
            when (it.first) {
                0 -> {
                    characteristic = bluetoothGatt?.getService(expandUuid(uuidSrvFlex))
                        ?.getCharacteristic(expandUuid(uuidChrFlex))
                    bluetoothGatt?.setCharacteristicNotification(characteristic, it.second)
                    descriptor = characteristic?.getDescriptor(expandUuid(uuidDscFlex))?.apply {
                        value = if (it.second) {
                            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        } else {
                            BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                        }
                    }
                    bluetoothGatt?.writeDescriptor(descriptor)
                    Log.i(TAG, "NOTIFICATION FLEX: ${it.second}")
                }
                1 -> {
                    characteristic = bluetoothGatt?.getService(expandUuid(uuidSrvMCU))
                        ?.getCharacteristic(expandUuid(uuidChrMCU))
                    bluetoothGatt?.setCharacteristicNotification(characteristic, it.second)
                    descriptor = characteristic?.getDescriptor(expandUuid(uuidDscMCU))?.apply {
                        value = if (it.second) {
                            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        } else {
                            BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                        }
                    }
                    bluetoothGatt?.writeDescriptor(descriptor)
                    Log.i(TAG, "NOTIFICATION MCU: ${it.second}")
                }
                else -> {}
            }
        }
    }

    fun readCharacteristic(uuidSrv: String, uuidChr: String){
        val characteristic =bluetoothGatt?.getService(expandUuid(uuidSrv))?.getCharacteristic(
            expandUuid(
                uuidChr
            )
        )
        bluetoothGatt?.readCharacteristic(characteristic)
    }

    fun writeCharacteristic(uuidSrv: String, uuidChr: String, data: Byte){
        val characteristic =bluetoothGatt?.getService(expandUuid(uuidSrv))?.getCharacteristic(
            expandUuid(
                uuidChr
            )
        )?.apply {
            value = byteArrayOf(data)
        }
        bluetoothGatt?.writeCharacteristic(characteristic)
    }

    private val scanCallback = object : ScanCallback(){

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                deviceFound(it.device)
            }
        }
    }
    private fun deviceFound(device: BluetoothDevice?) {
       if (bluetoothGatt == null) {
           Log.i(TAG, "DEVICE FOUND")
           bluetoothGatt=device?.connectGatt(context, false, gattCallback)
       }
    }
    fun isConnected(): Boolean{
        return connected
    }
    fun isNotConnected() :Boolean{
        return !connected
    }

    private val gattCallback = object  : BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {

            val strStatus = if (status == 0) "GRANTED" else "GATT ERROR $status"
            val strNewState = if (newState == 0) "DISCONNECTED" else "CONNECTED"
            Log.i(TAG, "STATUS: $strStatus")
            Log.i(TAG, "STATE: $strNewState")
            if (newState == BluetoothGatt.STATE_CONNECTED){
                deviceName = gatt?.device?.name.toString()
                connected = true
                Log.i(TAG, "CONNECTED TO $deviceName")
                gatt?.discoverServices()
            }else if (newState == BluetoothGatt.STATE_DISCONNECTED){
                connected=false
                bluetoothGatt=null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            writeNextDescriptor()
        }
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Log.i(
                TAG,
                characteristic?.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0).toString()
            )
        }
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            when(characteristic?.uuid){
                expandUuid(uuidChrFlex) -> setFlex(characteristic)
                expandUuid(uuidChrMCU) -> setMCU(characteristic)
            }
        }
    }

    private fun setMCU(characteristic: BluetoothGattCharacteristic) {
        for(i in 0..3){
            quaternion[i] =ByteBuffer.wrap(byteArrayOf(characteristic.value[3+(i*4)], characteristic.value[2+(i*4)], characteristic.value[1+(i*4)], characteristic.value[0+(i*4)])).getFloat(0)
        }


        Log.i(
            TAG2, "current MCU data: " +
                    quaternion[0].toString()+ " - " +
                    quaternion[1].toString()+ " - " +
                    quaternion[2].toString()+ " - " +
                    quaternion[3].toString()
        )

        newVector=rotateVector(refVector,quaternion)
        for (i in 0..2) {
            difVector[i] = oldVector[i] - newVector[i]
        }
        oldVector=newVector


        if(flexFlag){
            process?.let {
                if(it(fingers,newVector)!=led){
                    led = it(fingers, newVector)
                    writeCharacteristic(uuidSrvLed, uuidChrLed, led.toByte())
                }
            }
            flexFlag=false
            mcuFlag = false
        }else{
            mcuFlag = true
        }

    }
    fun openSession(myProcess: (IntArray, FloatArray) -> Int){
        // invoke regular function using local name
        process = myProcess
    }
    fun closeSession(){
        process = null
    }

    private fun setFlex(characteristic: BluetoothGattCharacteristic) {

        for (i in 0..4){
            fingers[i] = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, i)
        }



        Log.i(
            TAG2, "current flex data: " +
                    characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                        .toString() + " - " +
                    characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1)
                        .toString() + " - " +
                    characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2)
                        .toString() + " - " +
                    characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3)
                        .toString() + " - " +
                    characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 4)
                        .toString()
        )
        if(flexFlag){
            process?.let {
                if(it(fingers,newVector)!=led){
                    led = it(fingers, newVector)
                    writeCharacteristic(uuidSrvLed, uuidChrLed, led.toByte())
                }
            }
            flexFlag = false
            mcuFlag  = false
        }else{
            flexFlag = true
        }

    }


    private fun expandUuid(shortCode16: String): UUID {
        return UUID.fromString("0000$shortCode16-${Companion.uuidBase}")
    }
    private fun rotateVector(vector: FloatArray, hamiltonForm: FloatArray): FloatArray
    {
        val rotated = floatArrayOf(0f, 0f, 0f)
        val r11 :Float = hamiltonForm[0]*hamiltonForm[0]+hamiltonForm[1]*hamiltonForm[1]-hamiltonForm[2]*hamiltonForm[2]-hamiltonForm[3]*hamiltonForm[3]
        val r12 :Float = 2*(hamiltonForm[1]*hamiltonForm[2]-hamiltonForm[3]*hamiltonForm[0]);
        val r13 :Float = 2*(hamiltonForm[1]*hamiltonForm[3]+hamiltonForm[2]*hamiltonForm[0]);
        val r21 :Float = 2*(hamiltonForm[1]*hamiltonForm[2]+hamiltonForm[3]*hamiltonForm[0]);
        val r22 :Float = hamiltonForm[0]*hamiltonForm[0]-hamiltonForm[1]*hamiltonForm[1]+hamiltonForm[2]*hamiltonForm[2]-hamiltonForm[3]*hamiltonForm[3];
        val r23 :Float = 2*(hamiltonForm[2]*hamiltonForm[3]-hamiltonForm[1]*hamiltonForm[0]);
        val r31 :Float = 2*(hamiltonForm[1]*hamiltonForm[3]-hamiltonForm[2]*hamiltonForm[0]);
        val r32 :Float = 2*(hamiltonForm[2]*hamiltonForm[3]+hamiltonForm[1]*hamiltonForm[0]);
        val r33 :Float = hamiltonForm[0]*hamiltonForm[0]-hamiltonForm[1]*hamiltonForm[1]-hamiltonForm[2]*hamiltonForm[2]+hamiltonForm[3]*hamiltonForm[3];
        rotated[0] = r11*vector[0] + r12*vector[1] + r13*vector[2]
        rotated[1] = r21*vector[0] + r22*vector[1] + r23*vector[2]
        rotated[2] = r31*vector[0] + r32*vector[1] + r33*vector[2]
        return rotated;
    }
    companion object {
        const val uuidBase = "0000-1000-8000-00805F9B34FB"
        const val uuidSrvFlex ="0100"
        const val uuidSrvRst = "0200"
        const val uuidSrvMCU = "0300"
        const val uuidSrvLed = "0400"
        const val uuidSrvBatery = "180F"
        const val uuidChrFlex = "0110"
        const val uuidChrRst = "0210"
        const val uuidChrMCU = "0310"
        const val uuidChrLed = "0410"
        const val uuidChrBatery = "2A19"
        const val uuidDscFlex = "2902"
        const val uuidDscMCU = "2902"
        const val uuidDscRst = "0211"
        const val uuidDscLed = "0411"
        const val uuidDscBatery1 = "2901"
        const val uuidDscBatery2 = "2902"
    }

}