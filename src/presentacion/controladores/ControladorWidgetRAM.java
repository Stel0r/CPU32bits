package presentacion.controladores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import logica.EventLog;
import logica.SistemaSAP;
import presentacion.Modelo;
import presentacion.vistas.VistaPanelAssembler;
import presentacion.vistas.VistaPanelCPU;
import presentacion.vistas.VistaWidgetRAM;

public class ControladorWidgetRAM implements interfaces.IRAMObserver, ActionListener {

    private final VistaWidgetRAM widgetRAM;
    private VistaPanelCPU view;
    private final SistemaSAP sistema;    
    private final Modelo modelo;

    public ControladorWidgetRAM(VistaWidgetRAM aThis) {
        widgetRAM = aThis;
        view = widgetRAM.getView();
        sistema = widgetRAM.getSistema();
        modelo = aThis.getModelo();
    }

    @Override
    // Si un valor en la memoria ha cambiado, entonces hay que redibujar
    public void cambiaValorRAM(int address) {

        // Iterar sobre todos los bits en la posición de memoria actual
        if(address < 16){
            for (int i = 0; i <= 7; i++) {
                widgetRAM.getBtnArrayBotones()[address][i].setText(Integer.toHexString(this.buscarEnRAM(address,  7 - i)).toUpperCase());

                // Compruebar si es el valor MAR, en cuyo caso se necesita un color para resaltar
                if (widgetRAM.isDebeResaltarMAR() && address == widgetRAM.getValorMAR()) {
                    widgetRAM.getBtnArrayBotones()[address][i].setBackground(widgetRAM.COLOR_MAR);
                } else {
                    widgetRAM.getBtnArrayBotones()[address][i].setBackground(widgetRAM.getBtnArrayBotones()[address][i].getText().equals("1") ? widgetRAM.COLOR_ON : widgetRAM.COLOR_OFF);
                }

                // Si estamos en la posición más a la derecha, mantenga el borde
                if (i == 7) {
                    widgetRAM.getBtnArrayBotones()[address][i].setBorder(widgetRAM.RIGHT_BORDER);
                } else {
                    widgetRAM.getBtnArrayBotones()[address][i].setBorder(null);
                }

                // Si estamos en la fila inferior, mantener el borde.
                if (address == 15) {
                    if (i == 7) {
                        widgetRAM.getBtnArrayBotones()[address][i].setBorder(widgetRAM.BOTTOM_RIGHT_BORDER);
                    } else {
                        widgetRAM.getBtnArrayBotones()[address][i].setBorder(widgetRAM.BOTTOM_BORDER);
                    }
                }
            }
        }
    }

    public void cambioMAR(int v) {
        // Si no estamos en modo resaltado
        if (!widgetRAM.isDebeResaltarMAR()) {
            cambiaValorRAM(v);
            return;
        }

        // Coge el antiguo valor MAR
        int oldVal = widgetRAM.getValorMAR();

        // Pintar el nuevo valor con el color correcto
        widgetRAM.setValorMAR(v);
        cambiaValorRAM(widgetRAM.getValorMAR());

        // Elimina la coloración especial del antiguo valor MAR
        cambiaValorRAM(oldVal);
    }

    @Override
    // Responde al clic del botón que indica un cambio de bit en la memoria
    public void actionPerformed(ActionEvent e) {
        // Si el usuario desea alternar el resaltado MAR en la parte de RAM del widget
        if (e.getActionCommand().contentEquals("toggleMAR")) {
            // Alternar valor de estado
            widgetRAM.setDebeResaltarMAR (!widgetRAM.isDebeResaltarMAR());

            // Actualizar etiqueta de botón
            widgetRAM.getHighlightMarButton().setText(widgetRAM.isDebeResaltarMAR() ? widgetRAM.MAR_ON_LABEL : widgetRAM.MAR_OFF_LABEL);

            // Actualizar visualización
            this.cambioMAR(widgetRAM.getValorMAR());

            return;
        }

        // Si el programa se está ejecutando, primero detenerlo
        if (this.modelo.isEjecutandoPrograma()) {
            ActionEvent x = new ActionEvent("", 5, "autoplay");
            this.view.getControl().actionPerformed(x);

            // Dormir para que el registro de eventos no se agrupe
            try {
                Thread.sleep(25);
            } catch (InterruptedException e1) {
                EventLog.getEventLog().addEntrada("Error en sleep para 100 ms");
            }
        }

        // Si el usuario quiere abrir el ensamblador
        if (e.getActionCommand().contentEquals("openAssembler")) {
            // Crear instancia del ensamblador
            VistaPanelAssembler vistaPanelAssembler = new VistaPanelAssembler(modelo, widgetRAM.getParentPanel());

            // Establecer la vista en el ensamblador (de la ventana actual)
            modelo.getVentanaPrincipal().setContentPane(vistaPanelAssembler);
            modelo.getVentanaPrincipal().pack();
            modelo.getVentanaPrincipal().setVisible(true);

            return;
        }

        // Si el usuario hace clic en el botón analizar programa
        if (e.getActionCommand().contentEquals("analyzeProgram")) {
            EventLog.getEventLog().addEntrada("=============");
            EventLog.getEventLog().addEntrada("[DIR]\t[INSTR]\t[DEC]");
            for (int i = 0; i < 16; i++) {
                this.sistema.analizarInstruccion(i);
            }
            EventLog.getEventLog().addEntrada("=============");
            return;
        }

        // Si el usuario hace clic en el botón borrar memoria
        if (e.getActionCommand().contentEquals("clearmem")) {
            // Obtener el contenido de la memoria
            int[] arr = this.sistema.getRAM().getData();

            for (int i = 0; i < 16; i++) {
                // Colocamos cada posición en 0
                arr[i] = 0;
            }

            // Obligar a la pantalla a volver a pintar dos veces, para manejar el retraso visual
            for (int i = 0; i < 16; i++) {
                this.cambiaValorRAM(i);
            }
            for (int i = 0; i < 16; i++) {
                this.cambiaValorRAM(i);
            }
            return;
        }

        // Si el usuario hace clic en el botón Mostrar los códigos de operación
        if (e.getActionCommand().contentEquals("showopcodes")) {
            EventLog.getEventLog().addEntrada("=============");
            EventLog.getEventLog().addEntrada("NOP\t0000");
            EventLog.getEventLog().addEntrada("LDA\t0001");
            EventLog.getEventLog().addEntrada("ADD\t0010");
            EventLog.getEventLog().addEntrada("SUB\t0011");
            EventLog.getEventLog().addEntrada("STA\t0100");
            EventLog.getEventLog().addEntrada("LDI\t0101");
            EventLog.getEventLog().addEntrada("JMP\t0110");
            EventLog.getEventLog().addEntrada("JC\t0111");
            EventLog.getEventLog().addEntrada("JZ\t1000");
            EventLog.getEventLog().addEntrada("OUT\t1110");
            EventLog.getEventLog().addEntrada("HLT\t1111");
            EventLog.getEventLog().addEntrada("=============");
            return;
        }

        // Si el usuario hace clic en el botón Cargar programa de demostración
        if (e.getActionCommand().contentEquals("loadcountprogram")) {
            // Toma la representación interna de la RAM
            int[] arr = this.sistema.getRAM().getData();

            // Primero borramos la memoria
            for (int i = 0; i < 16; i++) {
                // Coloca en cada posición 0
                // La pantalla vuelve a pintar
                this.sistema.getRAM().cambiarValor(i, 0);

            }

            // Actualizamos el contenido de la memoria con el programa demo
            // Nota: Este programa es un simple contador
            this.sistema.getRAM().cambiarValor(0, 0b00000101000000000000000000000000);
            this.sistema.getRAM().cambiarValor(1, 0b00000010000000000000000000001110);
            this.sistema.getRAM().cambiarValor(2, 0b00001110000000000000000000000000);
            this.sistema.getRAM().cambiarValor(3, 0b00000100000000000000000000001010);
            this.sistema.getRAM().cambiarValor(4, 0b00000110000000000000000000000001);
            this.sistema.getRAM().cambiarValor(14, 0b00000000000000000000000000000001);
            return;
        }

        // De lo contrario, el usuario debe haber solicitado un cambio de bit en algún lugar de la memoria.
        
        // Analizar la dirección de memoria
        int address = Byte.parseByte(e.getActionCommand().substring(0, e.getActionCommand().indexOf(",")));
        // Analizar el cambio de  bit en la posición 
        int bitPos = Byte.parseByte(e.getActionCommand().substring(e.getActionCommand().indexOf(",") + 1));
        bitPos = (int) (7 - bitPos);
        // Obtener el valor actual del bit agregar la posición modificada
        int currVal = buscarEnRAM(address, bitPos);
        // Obtenga el valor actual de la memoria en la dirección especificada
        int memVal = this.sistema.getRAM().getData()[address];

        // Determinar si necesitamos restar o sumar
        int newVal;
        if (currVal == 15) {
            // Restar   
            newVal = (int) (memVal - (Math.pow(16, bitPos)*15));
        } else {
            // Sumar
            
            newVal = (int) (memVal + Math.pow(16, bitPos));
        }
        this.sistema.getRAM().cambiarValor(address, newVal);

        // Informe al log
        logica.EventLog.getEventLog().addEntrada("Dirección de memoria " + address + " cambió a " + newVal);
    }
    
    // Función auxiliar para acceder a bits individuales en la memoria; Address: [0, 15]
    // bitPos: [0, 7]
    public int buscarEnRAM(int address, int bitPos) {
        String val = Integer.toHexString(this.sistema.getRAM().getData()[address]);
        val = "0".repeat(8-val.length())+val;
        return Integer.parseInt(val.charAt(7-bitPos)+"",16);
    }
}
