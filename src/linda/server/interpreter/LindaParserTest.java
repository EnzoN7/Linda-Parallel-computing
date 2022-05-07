package linda.server.file;

import linda.Tuple;
import linda.server.parser.LindaOperation;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class LindaFileTest {

    File tempFileOneLineRead, tempFileOneLineTake, tempFileOneLineWrite;

    LindaFileParser parser;

    public LindaFileTest() {
        FileWriter fileWriter;

        try {
            tempFileOneLineRead = File.createTempFile("tempFileOneLineRead", ".linda");
            tempFileOneLineTake = File.createTempFile("tempFileOneLineTake", ".linda");
            tempFileOneLineWrite = File.createTempFile("tempFileOneLineWrite", ".linda");

            fileWriter = new FileWriter(tempFileOneLineRead);
            fileWriter.write("READ [ true 2 'b' ]");
            fileWriter.close();

            fileWriter = new FileWriter(tempFileOneLineTake);
            fileWriter.write("TAKE [ true 2 'b' ]");
            fileWriter.close();

            fileWriter = new FileWriter(tempFileOneLineWrite);
            fileWriter.write("WRITE [ true 2 'b' ]");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    @Test
    public void basicRead() {
        try {
            parser = new LindaFileParser(tempFileOneLineRead.getPath());

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            var cmd = _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.READ);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void basicTake() {
        try {
            parser = new LindaFileParser(tempFileOneLineTake.getPath());

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            var cmd = _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.TAKE);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void basicWrite() {
        try {
            parser = new LindaFileParser(tempFileOneLineWrite.getPath());

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            var cmd = _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.WRITE);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
