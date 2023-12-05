package presentacion.vistas;

import java.awt.Dimension;

import javax.swing.JPanel;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

import interfaces.IRAMObserver;
import logica.Memoria;

public class VistaMemoria extends JPanel implements IRAMObserver {

    public JTable tabla;
    public Memoria RAM;
    public TableModelListener tableListener;

    public VistaMemoria(Memoria m){
        super();
        RAM = m;
        RAM.addRAMObserver(this);
        tableListener = new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                manejarCambio();
            }
            
        };
        crearTabla();
    }

    public void crearTabla(){
        this.removeAll();
        int[] ram = RAM.getData();
        DefaultTableModel model = new DefaultTableModel(new String[]{"Adress","+0","+32","+64","+96","+128","+160","+192","+224","+256","+288"},0);
        tabla = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new Dimension(1000,660));
        
        for (int i = 0;i<ram.length;i++){
            String adress = String.format("%X",i);
            adress = "0".repeat(6-adress.length())+adress;
            model.addRow(new String[]{"0x"+adress,String.format("%X",ram[i]),String.format("%X",ram[i+1]),String.format("%X",ram[i+2]),String.format("%X",ram[i+3]),String.format("%X",ram[i+4]),String.format("%X",ram[i+5]),String.format("%X",ram[i+6]),String.format("%X",ram[i+7]),String.format("%X",ram[i+8]),String.format("%X",ram[i+9])});
            i = i + 9;
        }

        model.addTableModelListener(tableListener);
        this.add(scrollPane);
        this.repaint();

        
    }

    @Override
    public void cambiaValorRAM(int address) {
        int row = address / 10;
        int column = (address % 10)+1;

        tabla.getModel().removeTableModelListener(tableListener);
        tabla.getModel().setValueAt(String.format("%X",RAM.getData()[address]), row, column);
        tabla.getModel().addTableModelListener(tableListener);
    }

    public void manejarCambio(){
        int row = tabla.getSelectedRow();
        int column = tabla.getSelectedColumn();

        String address =(String) tabla.getValueAt(row, 0);
        address = address.substring(2);
        int add = Integer.parseInt(address,16)+(column-1);
        String value = (String)tabla.getValueAt(row, column);
        if(value.matches("[0123456789ABCDEF]{0,8}")){
            RAM.cambiarValor(add, Integer.parseInt(value,16));
        }else{
            RAM.cambiarValor(add, Integer.parseInt("0",16));
        }
        
    }
}
