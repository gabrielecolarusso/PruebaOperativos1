/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SISTEMA;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter; 
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import EDD.ListaEnlazada;
import EDD.Nodo;
import java.util.HashMap;
import com.google.gson.Gson;

public class SimulatedDisk {
    private boolean[] blockMap;
    private HashMap<Integer, String> blockToFileMap;
    private static final String INFO_PATH = "INFO/";
    private static final String DISK_FILE = INFO_PATH + "disk.json";
    private int totalBlocks;

    public SimulatedDisk(int totalBlocks) {
        this.blockMap = new boolean[totalBlocks];
        this.blockToFileMap = new HashMap<>();
        this.totalBlocks = totalBlocks;
        crearCarpetaInfo();
        // NO CARGAR al crear un nuevo disco
    }

    private void crearCarpetaInfo() {
        File folder = new File(INFO_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public boolean[] getBlockMap() {
        return blockMap;
    }

    public ListaEnlazada<Integer> allocateBlocks(int size, String fileName) {
        ListaEnlazada<Integer> allocatedBlocks = new ListaEnlazada<>();
        int count = 0;
        int totalBlocks = blockMap.length;
        int attempts = 0;
        int maxAttempts = totalBlocks * 10;

        while (count < size && attempts < maxAttempts) {
            int index = (int) (Math.random() * totalBlocks);
            if (!blockMap[index]) {
                blockMap[index] = true;
                allocatedBlocks.agregar(index);
                blockToFileMap.put(index, fileName);
                count++;
            }
            attempts++;
        }

        if (count < size) {
            releaseBlocks(allocatedBlocks);
            return null;
        }

        guardarEstadoDisco();
        return allocatedBlocks;
    }

    public void releaseBlocks(ListaEnlazada<Integer> blocks) {
        Nodo<Integer> actual = blocks.getCabeza();
        while (actual != null) {
            if (actual.dato >= 0 && actual.dato < blockMap.length) {
                blockMap[actual.dato] = false;
                blockToFileMap.remove(actual.dato);
            }
            actual = actual.siguiente;
        }
        guardarEstadoDisco();
    }

    public String getArchivoPorBloque(int bloque) {
        return blockToFileMap.getOrDefault(bloque, null);
    }

    public void guardarEstadoDisco() {
        try (Writer writer = new FileWriter(DISK_FILE)) {
            new Gson().toJson(blockMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarEstadoDisco() {
        File file = new File(DISK_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(DISK_FILE)) {
                boolean[] loadedBlockMap = new Gson().fromJson(reader, boolean[].class);
                if (loadedBlockMap != null && loadedBlockMap.length == blockMap.length) {
                    System.arraycopy(loadedBlockMap, 0, blockMap, 0, loadedBlockMap.length);
                    reconstruirBlockToFileMap();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void reconstruirBlockToFileMap() {
        blockToFileMap.clear();
    }

    public int getTotalBlocks() {
        return totalBlocks;
    }

    public void setTotalBlocks(int totalBlocks) {
        this.totalBlocks = totalBlocks;
    }
}