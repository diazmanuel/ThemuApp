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
import kotlin.experimental.and


class Ble(val context: Context){

    val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var deviceName: String? = null
    private val SCAN_PERIOD: Long = 10000
    private val TAG = "BLE"
    private val TAG2 = "NOTIFY"

    private var bluetoothGatt: BluetoothGatt? = null
    var connected = false
    var fingers = intArrayOf(0, 0, 0, 0, 0)
    var gestures :BitSet = BitSet(8)

    private var gesturesFlag: Boolean = false
    private var flexFlag: Boolean = false
    private var led = 0
    private var process : ((IntArray, BitSet) -> Int)? = null
    private var updateSymbol : ((IntArray, BitSet) -> Unit)? = null

    private val descQueue: Queue<Pair<Int,Boolean>> = LinkedList()

    fun start(){

        val handler = Handler()
        val filters = mutableListOf<ScanFilter>()
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

        filters.add(
            ScanFilter.Builder().setServiceUuid(ParcelUuid(expandUuid(uuidSrvFlex))).build()
        )
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(expandUuid(uuidSrvGesture))).build())
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(expandUuid(uuidSrvRst))).build())
        filters.add(ScanFilter.Builder().setServiceUuid(ParcelUuid(expandUuid(uuidSrvBatery))).build())
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
                    characteristic = bluetoothGatt?.getService(expandUuid(uuidSrvGesture))
                        ?.getCharacteristic(expandUuid(uuidChrGesture))
                    bluetoothGatt?.setCharacteristicNotification(characteristic, it.second)
                    descriptor = characteristic?.getDescriptor(expandUuid(uuidDscGesture))?.apply {
                        value = if (it.second) {
                            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        } else {
                            BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                        }
                    }
                    bluetoothGatt?.writeDescriptor(descriptor)
                    Log.i(TAG, "NOTIFICATION Gesture: ${it.second}")
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
                expandUuid(uuidChrGesture) -> setGesture(characteristic)
            }
        }
    }
    private fun setGesture(characteristic: BluetoothGattCharacteristic){
        gestures = BitSet.valueOf(byteArrayOf(characteristic.value[0]))
        if(flexFlag){
            process?.let { setLed(it(fingers,gestures)) }
            updateSymbol?.let { it(fingers,gestures) }
            flexFlag=false
            gesturesFlag = false
        }else{
            gesturesFlag = true
        }
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
        if(gesturesFlag){
            process?.let { setLed(it(fingers,gestures)) }
            updateSymbol?.let { it(fingers,gestures) }
            flexFlag = false
            gesturesFlag  = false
        }else{
            flexFlag = true
        }

    }
    fun setLed(newLed: Int){
        if (led!=newLed){
            writeCharacteristic(uuidSrvLed, uuidChrLed, newLed.toByte())
            led=newLed
        }
    }

    fun startUpdateSymbol(myUpdateSymbol: (IntArray, BitSet) -> Unit){
        updateSymbol=myUpdateSymbol
    }

    fun startProcess(myProcess: (IntArray, BitSet) -> Int){
        process = myProcess
    }
    fun stopProcess(){
        process=null
    }
    fun stopUpdateSymbol(){
        updateSymbol=null
    }

    private fun expandUuid(shortCode16: String): UUID {
        return UUID.fromString("0000$shortCode16-${Companion.uuidBase}")
    }

    companion object {
        const val uuidBase = "0000-1000-8000-00805F9B34FB"
        const val uuidSrvFlex ="0100"
        const val uuidSrvRst = "0200"
        const val uuidSrvGesture = "0300"
        const val uuidSrvLed = "0400"
        const val uuidSrvBatery = "180F"
        const val uuidChrFlex = "0110"
        const val uuidChrRst = "0210"
        const val uuidChrGesture = "0310"
        const val uuidChrLed = "0410"
        const val uuidChrBatery = "2A19"
        const val uuidDscFlex = "2902"
        const val uuidDscGesture = "2902"
        const val uuidDscRst = "0211"
        const val uuidDscLed = "0411"
        const val uuidDscBatery1 = "2901"
        const val uuidDscBatery2 = "2902"
    }

}