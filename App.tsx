/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, { useState } from 'react';
import type {PropsWithChildren} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  useColorScheme,
  View,
  NativeModules,
  ToastAndroid,
  Switch
} from 'react-native';

import {
  Colors,
  DebugInstructions,
  Header,
  LearnMoreLinks,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

const ToastService = NativeModules.ToastModule;

const { UsbSerial } = NativeModules;  // 👈 Asegurar que UsbSerial existe


type SectionProps = PropsWithChildren<{
  title: string;
}>;



function App(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  const [text, setText] = useState('');

  // UsbSerial.listDevices((count) => {
  //   console.log(`Dispositivos USB conectados: ${count}`);
  //  // ToastAndroid.show(count,10)
  // });

  console.log("Funciones disponibles en UsbSerial:", Object.keys(ToastService));

  // Solicitar permiso para usar USB
const requestUsbPermission = async () => {
  try {
    const response = await UsbSerial.requestUsbPermission();
    console.log(response);
  } catch (error) {
    console.error("Error solicitando permiso USB:", error);
  }
};

// Ejecutar las funciones
requestUsbPermission()
  .then(() => setTimeout(connectUsb, 2000)); // Esperar 2s para conceder permiso

// Conectar el USB
const connectUsb = async () => {
  try {
    const response = await UsbSerial.connectUsb();
    if(response==="USB Serial connected"){
     // sendOne("1");
    }
    
    console.log(response);
  } catch (error) {
    console.error("Error conectando USB:", error);
  }
};

// Enviar '1' al dispositivo USB
const sendOne = async (comando:String) => {
  try {
    const response = await UsbSerial.sendData("1");
    console.log(response);
    if(response == "Data sent: 1"){
     // sendZero(comando)
    }
  } catch (error) {
    console.error("Error enviando datos:", error);
  }
};

// Enviar '0' al dispositivo USB
const sendZero = async (comando:String) => {
  try {
    const response = await UsbSerial.sendData("0");
    console.log(response);
  } catch (error) {
    console.error("Error enviando datos:", error);
  }
};

// Cerrar conexión USB
const closeUsb = async () => {
  try {
    const response = await UsbSerial.closeUsb();
    console.log(response);
  } catch (error) {
    console.error("Error cerrando USB:", error);
  }
};

// Ejecutar las funciones
connectUsb();

//setTimeout(sendZero, 2000); // Enviar '0' después de 2 segundos
//setTimeout(closeUsb, 4000); // Cerrar conexión después de 4 segundos

const [isEnabled, setIsEnabled] = useState(false);


// Cambia el estado del switch y envía 1 o 0
const toggleSwitch = () => {
  const newValue = !isEnabled ? 1 : 0; // Si está apagado, enviamos 1, si está encendido, enviamos 0
  setIsEnabled(!isEnabled);

  if(newValue==1){
    sendOne("1")
  }else{
    sendZero("0")
  }
  //sendData(newValue);
};

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
     <Text>Encender LED</Text>
     
     <TextInput 
       placeholder="Type something..."
       value={text}
       onChangeText={setText}
     ></TextInput>

     <TouchableOpacity onPress={() => ToastService.showToast("Holasss")}>
      <Text style={{
        alignSelf:'center',
        marginTop:10,
        color:'green'
      }}>Boton Enviar</Text>
     </TouchableOpacity>

     <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Text style={{ fontSize: 20, marginBottom: 10 }}>
        Estado: {isEnabled ? "Encendido (1)" : "Apagado (0)"}
      </Text>
      <Switch
        trackColor={{ false: "#767577", true: "#81b0ff" }}
        thumbColor={isEnabled ? "#f5dd4b" : "#f4f3f4"}
        onValueChange={toggleSwitch}
        value={isEnabled}
      />
    </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
});

export default App;
