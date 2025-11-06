/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SISTEMA;

import javax.swing.*;
import EDD.DirectoryEntry;
import EDD.FileEntry;
import EDD.ListaEnlazada;
import EDD.Nodo;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class FileSystem {
    private DirectoryEntry root;
    private SimulatedDisk disk;
    private ProcessQueue processQueue;
    private DiskScheduler scheduler;
    private BufferCache buffer;
    private static final String INFO_PATH = "INFO/";
    private static final String FILE_NAME = INFO_PATH + "filesystem.json";
    private boolean processingActive;

    public FileSystem(int diskSize) {
        this.root = new DirectoryEntry("root");
        this.disk = new SimulatedDisk(diskSize);
        this.processQueue = new ProcessQueue();
        this.scheduler = new DiskScheduler(SchedulingPolicy.FIFO);
        this.buffer = new BufferCache(20, CachePolicy.LRU);
        this.processingActive = false;
        crearCarpetaInfo();
        cargarDesdeArchivo();
    }

    private void crearCarpetaInfo() {
        File folder = new File(INFO_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public DirectoryEntry getRoot() {
        return root;
    }

    public SimulatedDisk getDisk() {
        return disk;
    }

    public ProcessQueue getProcessQueue() {
        return processQueue;
    }

    public DiskScheduler getScheduler() {
        return scheduler;
    }

    public BufferCache getBuffer() {
        return buffer;
    }

    public void setSchedulingPolicy(SchedulingPolicy policy) {
        scheduler.setPolicy(policy);
        AuditLog.registrarAccion("Sistema", "📋 Cambió política de planificación a " + policy);
    }

    public void setCachePolicy(CachePolicy policy) {
        buffer.setPolicy(policy);
        AuditLog.registrarAccion("Sistema", "💾 Cambió política de buffer a " + policy);
    }

    public void createFile(String path, String name, int size, String usuario) {
        IORequest request = new IORequest(Operation.CREATE, path, name, size);
        request.setBlockPosition((int)(Math.random() * disk.getTotalBlocks()));
        IOProcess process = new IOProcess("CREATE_" + name, request, usuario);
        processQueue.addProcess(process);
        AuditLog.registrarAccion(usuario, "📋 Proceso P" + process.getId() + " creado para crear archivo '" + name + "'");
    }

    public void createDirectory(String path, String name, String usuario) {
        DirectoryEntry dir = getDirectory(path);
        if (dir != null) {
            if (existeDirectorio(path, name)) {
                JOptionPane.showMessageDialog(null, "❌ El directorio '" + name + "' ya existe en '" + path + "'.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DirectoryEntry newDir = new DirectoryEntry(name);
            dir.addDirectory(newDir);
            AuditLog.registrarAccion(usuario, "📂 Creó el directorio '" + name + "' en '" + path + "'");
            guardarEnArchivo();
        } else {
            JOptionPane.showMessageDialog(null, "❌ El directorio '" + path + "' no existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteFile(String path, String name, String usuario) {
        IORequest request = new IORequest(Operation.DELETE, path, name);
        request.setBlockPosition((int)(Math.random() * disk.getTotalBlocks()));
        IOProcess process = new IOProcess("DELETE_" + name, request, usuario);
        processQueue.addProcess(process);
        AuditLog.registrarAccion(usuario, "📋 Proceso P" + process.getId() + " creado para eliminar archivo '" + name + "'");
    }

    public void updateFile(String path, String name, String newContent, String usuario) {
        IORequest request = new IORequest(Operation.UPDATE, path, name, newContent);
        request.setBlockPosition((int)(Math.random() * disk.getTotalBlocks()));
        IOProcess process = new IOProcess("UPDATE_" + name, request, usuario);
        processQueue.addProcess(process);
        AuditLog.registrarAccion(usuario, "📋 Proceso P" + process.getId() + " creado para actualizar archivo '" + name + "'");
    }

    public void processNextIO() {
        if (processingActive || processQueue.isEmpty()) {
            return;
        }

        processingActive = true;
        IOProcess process = scheduler.scheduleNext(processQueue.getQueue());
        
        if (process != null) {
            process.setState(ProcessState.RUNNING);
            executeProcess(process);
        }
        processingActive = false;
    }

    private void executeProcess(IOProcess process) {
        IORequest request = process.getIoRequest();
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        switch (request.getOperation()) {
            case CREATE:
                executeCreate(request, process);
                break;
            case DELETE:
                executeDelete(request, process);
                break;
            case UPDATE:
                executeUpdate(request, process);
                break;
            case READ:
                executeRead(request, process);
                break;
        }

        process.setState(ProcessState.TERMINATED);
        AuditLog.registrarAccion("Sistema", "✅ Proceso P" + process.getId() + " terminado");
    }

    private void executeCreate(IORequest request, IOProcess process) {
        DirectoryEntry dir = getDirectory(request.getPath());
        if (dir != null) {
            if (existeArchivo(request.getPath(), request.getFileName())) {
                process.setState(ProcessState.BLOCKED);
                JOptionPane.showMessageDialog(null, "❌ El archivo ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ListaEnlazada<Integer> allocatedBlocks = disk.allocateBlocks(request.getFileSize(), request.getFileName());
            if (allocatedBlocks != null) {
                FileEntry file = new FileEntry(request.getFileName(), request.getFileSize());
                file.blocks = allocatedBlocks;
                dir.addFile(file);
                
                // Agregar bloques al buffer
                Nodo<Integer> actualBlock = allocatedBlocks.getCabeza();
                while (actualBlock != null) {
                    buffer.put(actualBlock.dato, request.getFileName(), new byte[1024]);
                    actualBlock = actualBlock.siguiente;
                }
                
                AuditLog.registrarAccion(process.getUsuario(), "📂 Creó el archivo '" + request.getFileName() + "' en '" + request.getPath() + "'");
                guardarEnArchivo();
            } else {
                process.setState(ProcessState.BLOCKED);
                JOptionPane.showMessageDialog(null, "❌ No hay suficiente espacio.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void executeDelete(IORequest request, IOProcess process) {
        DirectoryEntry dir = getDirectory(request.getPath());
        if (dir != null) {
            Nodo<FileEntry> actual = dir.files.getCabeza();
            while (actual != null) {
                if (actual.dato.name.equals(request.getFileName())) {
                    disk.releaseBlocks(actual.dato.blocks);
                    dir.files.eliminar(actual.dato);
                    AuditLog.registrarAccion(process.getUsuario(), "🗑 Eliminó el archivo '" + request.getFileName() + "'");
                    guardarEnArchivo();
                    return;
                }
                actual = actual.siguiente;
            }
        }
    }

    private void executeUpdate(IORequest request, IOProcess process) {
        DirectoryEntry dir = getDirectory(request.getPath());
        if (dir != null) {
            Nodo<FileEntry> actual = dir.files.getCabeza();
            while (actual != null) {
                if (actual.dato.name.equals(request.getFileName())) {
                    BackupManager.guardarVersion(request.getFileName(), request.getContent());
                    
                    // Actualizar en buffer si existe
                    Nodo<Integer> actualBlock = actual.dato.blocks.getCabeza();
                    while (actualBlock != null) {
                        CacheBlock cached = buffer.get(actualBlock.dato);
                        if (cached != null) {
                            cached.setData(request.getContent().getBytes());
                        }
                        actualBlock = actualBlock.siguiente;
                    }
                    
                    AuditLog.registrarAccion(process.getUsuario(), "✏️ Actualizó el archivo '" + request.getFileName() + "'");
                    guardarEnArchivo();
                    return;
                }
                actual = actual.siguiente;
            }
        }
    }

    private void executeRead(IORequest request, IOProcess process) {
        DirectoryEntry dir = getDirectory(request.getPath());
        if (dir != null) {
            Nodo<FileEntry> actual = dir.files.getCabeza();
            while (actual != null) {
                if (actual.dato.name.equals(request.getFileName())) {
                    // Intentar leer desde buffer primero
                    Nodo<Integer> actualBlock = actual.dato.blocks.getCabeza();
                    boolean foundInCache = false;
                    while (actualBlock != null) {
                        CacheBlock cached = buffer.get(actualBlock.dato);
                        if (cached != null) {
                            foundInCache = true;
                        }
                        actualBlock = actualBlock.siguiente;
                    }
                    
                    String cacheStatus = foundInCache ? " (HIT en buffer)" : " (MISS - leyendo de disco)";
                    AuditLog.registrarAccion(process.getUsuario(), "📖 Leyó el archivo '" + request.getFileName() + "'" + cacheStatus);
                    return;
                }
                actual = actual.siguiente;
            }
        }
    }

    private DirectoryEntry getDirectory(String path) {
        if (path.equals("/")) {
            return root;
        }

        String[] partes = path.split("/");
        DirectoryEntry actual = root;

        for (String parte : partes) {
            if (parte.isEmpty()) continue;

            actual = actual.buscarDirectorio(parte);
            if (actual == null) {
                return null;
            }
        }

        return actual;
    }

    public boolean existeArchivo(String path, String name) {
        DirectoryEntry dir = getDirectory(path);
        if (dir != null) {
            Nodo<FileEntry> actual = dir.files.getCabeza();
            while (actual != null) {
                if (actual.dato.name.equals(name)) {
                    return true;
                }
                actual = actual.siguiente;
            }
        }
        return false;
    }

    public boolean existeDirectorio(String path, String name) {
        DirectoryEntry dir = getDirectory(path);
        if (dir != null) {
            Nodo<DirectoryEntry> actual = dir.subDirectories.getCabeza();
            while (actual != null) {
                if (actual.dato.name.equals(name)) {
                    return true;
                }
                actual = actual.siguiente;
            }
        }
        return false;
    }

    public int calcularTamañoDirectorio(DirectoryEntry dir) {
        int total = 0;

        Nodo<FileEntry> archivos = dir.files.getCabeza();
        while (archivos != null) {
            total += archivos.dato.size;
            archivos = archivos.siguiente;
        }

        Nodo<DirectoryEntry> subdirs = dir.subDirectories.getCabeza();
        while (subdirs != null) {
            total += calcularTamañoDirectorio(subdirs.dato);
            subdirs = subdirs.siguiente;
        }
        return total;
    }

    public int obtenerPrimerBloqueDirectorio(DirectoryEntry dir) {
        Nodo<FileEntry> archivos = dir.files.getCabeza();
        if (archivos != null && archivos.dato.blocks.getCabeza() != null) {
            return archivos.dato.blocks.obtener(0);
        }

        Nodo<DirectoryEntry> subdirs = dir.subDirectories.getCabeza();
        while (subdirs != null) {
            int bloque = obtenerPrimerBloqueDirectorio(subdirs.dato);
            if (bloque != -1) return bloque;
            subdirs = subdirs.siguiente;
        }

        return -1;
    }

    public void guardarEnArchivo() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cargarDesdeArchivo() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            root = gson.fromJson(reader, DirectoryEntry.class);
            corregirEstructura(root);
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private void corregirEstructura(DirectoryEntry dir) {
        if (dir.files == null) {
            dir.files = new ListaEnlazada<>();
        }
        if (dir.subDirectories == null) {
            dir.subDirectories = new ListaEnlazada<>();
        }

        Nodo<DirectoryEntry> actual = dir.subDirectories.getCabeza();
        while (actual != null) {
            corregirEstructura(actual.dato);
            actual = actual.siguiente;
        }
    }

    public String restoreFile(String fileName, String backupFile) {
        return BackupManager.restaurarVersion(fileName, backupFile);
    }

    public void changeUserMode(boolean isAdmin) {
        String modo = isAdmin ? "Administrador" : "Usuario";
        AuditLog.registrarAccion("Sistema", "🔄 Cambió el modo de usuario a " + modo);
    }

    public void moverArchivo(String pathOrigen, String fileName, String pathDestino, String usuario) {
        DirectoryEntry origen = getDirectory(pathOrigen);
        DirectoryEntry destino = getDirectory(pathDestino);

        if (origen == null || destino == null) {
            JOptionPane.showMessageDialog(null, "❌ Directorio no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Nodo<FileEntry> actual = origen.files.getCabeza();
        while (actual != null) {
            if (actual.dato.name.equals(fileName)) {
                origen.files.eliminar(actual.dato);
                destino.addFile(actual.dato);
                AuditLog.registrarAccion(usuario, "📂 Movió el archivo '" + fileName + "' a '" + pathDestino + "'");
                guardarEnArchivo();
                return;
            }
            actual = actual.siguiente;
        }

        JOptionPane.showMessageDialog(null, "❌ Archivo no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void borrarTodo() {
        root = new DirectoryEntry("root");
        disk = new SimulatedDisk(disk.getTotalBlocks());
        processQueue = new ProcessQueue();
        buffer.clear();
        guardarEnArchivo();
        disk.guardarEstadoDisco();
    }
}