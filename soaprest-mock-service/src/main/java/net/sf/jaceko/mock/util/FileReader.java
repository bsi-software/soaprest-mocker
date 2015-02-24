/**
 *
 *     Copyright (C) 2012 Jacek Obarymski
 *
 *     This file is part of SOAP/REST Mock Service.
 *
 *     SOAP/REST Mock Service is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License, version 3
 *     as published by the Free Software Foundation.
 *
 *     SOAP/REST Mock Service is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SOAP/REST Mock Service; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jaceko.mock.util;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class FileReader {
  private static final Logger LOG = Logger.getLogger(FileReader.class);

  private String charsetName = Charset.defaultCharset().name();

  public String readFileContents(final String fileName) {
    final StringBuilder text = new StringBuilder();
    final String newLine = System.getProperty("line.separator");
    Scanner scanner = null;
    try {
      final InputStream resourceAsStream = FileReader.class.getClassLoader().getResourceAsStream(fileName);
      if (resourceAsStream == null) {
        LOG.error("File not found: " + fileName);
        return null;
      }
      else {
        if (LOG.isInfoEnabled()) {
          LOG.info(fileName + " found in classpath");
        }
      }
      scanner = new Scanner(resourceAsStream, charsetName);
      while (scanner.hasNextLine()) {
        text.append(scanner.nextLine() + newLine);
      }
    }
    catch (final Exception e) {
      LOG.error("Problem reading file : " + fileName, e);
      return null;
    }
    finally {
      if (scanner != null) {
        scanner.close();
      }
    }

    return text.toString();
  }

  public void setCharsetName(String charsetName) {
    this.charsetName = charsetName;
  }
}
