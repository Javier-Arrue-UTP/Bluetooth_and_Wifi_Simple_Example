package com.example.investigacion2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.investigacion2.Adaptador.DeviceListAdapter;
import java.util.ArrayList;
import java.util.Set;


public class BluetoothActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    private BluetoothAdapter bluetoothAdapter;

    TextView textViewEstado, textViewEncontrados,textViewEmparejados;
    ImageView bluetoothIcon;
    ListView lvNewDevices;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        inicializar();

        disponibilidadBluetooth();
        setBluetoothIcon();


        //Filtro intent que captura el cambio del estado del bond
        //cuando ocurra el emparejamiento
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //Registramos otro reciever
        registerReceiver(broadcastReceiver, filter);

        lvNewDevices.setOnItemClickListener(this);

    }

    private void inicializar() {

        textViewEstado = (TextView) findViewById(R.id.estadoBluetooth);
        textViewEncontrados = (TextView) findViewById(R.id.dispositivosEncontrados);
        bluetoothIcon = (ImageView) findViewById(R.id.bluetoothIcon);
        textViewEmparejados = (TextView)findViewById(R.id.txtEmparejados);

        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void disponibilidadBluetooth() {

        if (bluetoothAdapter == null) {
            textViewEstado.setText("El Blueetooth no esta disponible");
        } else {
            textViewEstado.setText("El Blueetooth está disponible");
        }

    }

    //Seteando Imagen de acuerdo a su estado -> on/off
    private void setBluetoothIcon() {
        bluetoothIcon.setImageResource(R.drawable.bluetooth_off);

        if(bluetoothAdapter != null)
        {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothIcon.setImageResource(R.drawable.bluetooth_on);
            }
        }

    }


    public void btnOn(View view) {

        if(bluetoothAdapter == null){
            showToast("No puede utilizar Bluetooth");
        }
        else if (!bluetoothAdapter.isEnabled()) {
            showToast("Encendiendo Bluetooth");

            //Intent para prender bluetooth
            Intent intent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        } else {
            showToast("El Bluetooth ya esta encendido");
        }

    }

    public void btnOff(View view) {

        if(bluetoothAdapter == null){
            showToast("No puede utilizar Bluetooth");
        }
        else if (bluetoothAdapter.isEnabled()) {

            bluetoothAdapter.disable(); //Apagando bluetooth
            showToast("Apagando Bluetooth");
            bluetoothIcon.setImageResource(R.drawable.bluetooth_off);
        } else {
            showToast("El Bluetooth ya esta apagado");
        }

    }

    public void btnhacerVisible(View view) {

        if (!bluetoothAdapter.isDiscovering()) {
            showToast("Haciendo el dispositivo visible para otros");

            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(intent, REQUEST_DISCOVER_BT);
        }
    }

    public void btnDescubrirDispositivos(View view) {
        //Si no esta en modo busqueda, procede a buscar.
        if(!bluetoothAdapter.isDiscovering()){
            showToast("Buscando dispositivos...");
            bluetoothAdapter.startDiscovery();

            //Intent Filter: filtro para interceptar los cambios del bluetooth
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);

            //Rellenar ListView con el mBroadcastReciever2
            registerReceiver(mBroadcastReceiver2, discoverDevicesIntent);
        }
    }

    public void btnDispositivosEmparejados(View view){

        if(bluetoothAdapter.isEnabled()){

            textViewEmparejados.setText("Dispositivos emparejados");
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

            for(BluetoothDevice device: devices){
                textViewEmparejados.append("\n"+"\n - Dispositivo: " + device.getName());
            }

        }
        else{
            //Bluetooth apagado.
            showToast("Enciende el bluetooth para ver dispositivos emparejados");
        }
    }


    //Metodo para cuando el dispositivo pregunta si desea permitir al programa
    //encender el bluetooth
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    //bluetooth esta prendido
                    bluetoothIcon.setImageResource(R.drawable.bluetooth_on);
                    showToast("Bluetooth esta encendido");
                } else {
                    //usuario denego el encendido del bluetooth
                    showToast("No se pudo encender el bluetooth");
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();

    }


    /**
     * Este BroadcastReceiver nos permitira saber los cambios en el estado que esten ocurriendo.
     *
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            //Accion
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    showToast("Dispositivo emparejado");
                }
                //case2: creating a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    showToast("Enparejando dispositivo");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    showToast("Rompiendo emparejamiento");
                }
            }
        }
    };

    //BroadcastReceiver para detectar dispositivos e insertarlos al listview
    private BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("TAG", "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){

                //Recibiendo dispositivo por el getParcelableExtra()
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);

                //Añadiendo dispositivo al listview
                mBTDevices.add(device);
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);

                showToast(device.getName()+device.getAddress());

                textViewEncontrados.setText("Dispositivos encontrados");
            }
        }
    };


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Si hace click en emparejar, cancelamos la busqueda de dispositivos, ya que carga mucho la memoria
        bluetoothAdapter.cancelDiscovery();

        //Registramos el nombre
        String deviceName = mBTDevices.get(i).getName();

        //Creamos el bond (emparejamiento).
        //Antes chequeamos que la version sea mayor a API 18
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d("TAG", "Intentando emparejar con:  " + deviceName);
            mBTDevices.get(i).createBond();
        }
    }



}