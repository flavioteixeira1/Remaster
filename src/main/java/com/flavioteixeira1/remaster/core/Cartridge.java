package com.flavioteixeira1.remaster.core;

import java.awt.*;
import java.io.*;

public final class Cartridge {
  private File romFile;
  private FileInputStream fileStream;
  private FileDialog openDialog;
  private String fileName, fullPath;
  public byte[] romData;
  public int romSize, numPages;
  private boolean romLoaded;

  public Cartridge() {
  	romLoaded = false;
  }

  public void showOpenDialog(Frame parent) {
    openDialog = new FileDialog(parent, "Abrir ROM", FileDialog.LOAD);
    openDialog.setDirectory("./");
    openDialog.setFile("*.sms;*.gg");
    openDialog.show();

    fileName = openDialog.getFile();
    
    if(fileName != null && fileName.trim().length() != 0) {
      fullPath = openDialog.getDirectory() + fileName;
      loadFromFile(fullPath);
    }
    else {
      romLoaded = false;
    }

    try { if (fileStream != null) fileStream.close(); }
    catch(Exception e) {}
    fileStream = null;
    openDialog = null;
  }

  /**
   * Loads a ROM directly from a given full path (used by "Recentes" menu).
   * Returns true if load succeeded.
   */
  public boolean loadFromFile(String path) {
    if (path == null) return false;
    File f = new File(path);
    if (!f.exists() || !f.isFile()) {
      romLoaded = false;
      return false;
    }

    try {
      romFile = f;
      fileStream = new FileInputStream(romFile);
      fileName = romFile.getName();
      fullPath = romFile.getAbsolutePath();
      romSize = (int)romFile.length();
      if(romSize % 4096 != 0) { // Header detected. Skip it.
        romSize -= 512;
        romData = new byte[romSize];
        numPages = romSize / 16384;
        try { fileStream.skip(512); }
        catch(IOException e) { System.err.println("CRITICAL I/O ERROR: file error"); System.exit(1); }
      }
      else {
        romData = new byte[romSize];
        numPages = romSize / 16384;
      }

      try {
        fileStream.read(romData);
        romLoaded = true;
        System.out.println("CARTRIDGE: Successfully loaded " + Integer.toString(romSize) + " bytes from " + fileName);
      }
      catch(IOException e) { System.err.println("CRITICAL I/O ERROR: File cannot be loaded!"); System.exit(1); }
    }
    catch(FileNotFoundException e) {
      fileName = null;
      fullPath = null;
      romLoaded = false;
      System.err.println("CARTRIDGE ERROR: File not found.");
    }
    finally {
      try { if (fileStream != null) fileStream.close(); }
      catch(Exception e) {}
      fileStream = null;
    }

    return romLoaded;
  }

  public String getFileName() {
    return fileName;
  }

  public String getFullPath() {
    return fullPath;
  }

  public int getRomSize() {
    return romSize;
  }

  public boolean isLoaded() {
    return romLoaded;
  }
  
  public void unload() {
  	romLoaded = false;
  }
}