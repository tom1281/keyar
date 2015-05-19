# keyar
KEYar is not my first attempt to connect Bluetooth Low Energy V4.0 and Augmented Reality technology, although it is the first one, which I had done from the beginning by myself. The most clues I get from Android developers site, where the whole topic is fairly clear explained:
https://developer.android.com/guide/topics/connectivity/bluetooth-le.html

To understand step by step, how to send data to a peripheral device, we can follow [this pattern:](
http://stackoverflow.com/questions/23367726/android-ble-cant-receive-gatt-characteristic-notification-from-device)
> 1. You try to connect
> 2. You get a callback indicating it is connected
> 3. You discover services
> 4. You are told services are discovered
> 5. You get the characteristics
> 6. For each characteristic you get the descriptors
> 7. For the descriptor you set it to enable notification/indication with BluetoothGattDescriptor.setValue()
> 8. You write the descriptor with BluetoothGatt.writeDescriptor()
> 9. You enable notifications for the characteristic locally with BluetoothGatt.setCharacteristicNotification(). Without this you won't get called back.
> 10. You get notification that the descriptor was written
> 11. Now you can write data to the characteristic. All of the characteristic and descriptor configuration has do be done before anything is written to any characteristic.

At this stage, this code allows to send data to Arduino with a BLE module HM-10. In the nearest future I'll try to use Vuforia SDK to create a virtual keyboard - thereby we can for example make a door lock.
