/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class LittleEndianDataOutputStream extends FilterOutputStream implements DataOutput {

  public LittleEndianDataOutputStream(OutputStream out) {
    super(new DataOutputStream(out));
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    out.write(b, off, len);
  }

  @Override
  public void writeBoolean(boolean v) throws IOException {
    ((DataOutputStream) out).writeBoolean(v);
  }

  @Override
  public void writeByte(int v) throws IOException {
    ((DataOutputStream) out).writeByte(v);
  }

  @Override
  public void writeShort(int v) throws IOException {
    out.write(0xFF & v);
    out.write(0xFF & (v >> 8));
  }

  @Override
  public void writeChar(int v) throws IOException {
    writeShort(v);
  }

  @Override
  public void writeInt(int v) throws IOException {
    out.write(0xFF & v);
    out.write(0xFF & (v >> 8));
    out.write(0xFF & (v >> 16));
    out.write(0xFF & (v >> 24));
  }

  @Override
  public void writeLong(long v) throws IOException {
    out.write((int) (0xFF & v));
    out.write((int) (0xFF & (v >> 8)));
    out.write((int) (0xFF & (v >> 16)));
    out.write((int) (0xFF & (v >> 24)));
    out.write((int) (0xFF & (v >> 32)));
    out.write((int) (0xFF & (v >> 40)));
    out.write((int) (0xFF & (v >> 48)));
    out.write((int) (0xFF & (v >> 56)));
  }

  @Override
  public void writeFloat(float v) throws IOException {
    writeInt(Float.floatToIntBits(v));
  }

  @Override
  public void writeDouble(double v) throws IOException {
    writeLong(Double.doubleToLongBits(v));
  }

  @Override
  public void writeBytes(String s) throws IOException {
    ((DataOutputStream) out).writeBytes(s);
  }

  @Override
  public void writeChars(String s) throws IOException {
    for (int i = 0; i < s.length(); i++) {
      writeChar(s.charAt(i));
    }
  }

  @Override
  public void writeUTF(String str) throws IOException {
    ((DataOutputStream) out).writeUTF(str);
  }

  @Override
  public void close() throws IOException {
    out.close();
  }

}
