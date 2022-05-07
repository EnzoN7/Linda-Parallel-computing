package linda.server.interpreter;

import linda.Linda;
import linda.Tuple;
import linda.callbacks.HelloCallback;
import linda.server.interpreter.commands.LindaBasicCommand;
import linda.server.interpreter.commands.LindaEventRegisterCommand;
import linda.server.interpreter.parsers.LindaFileParser;
import linda.server.interpreter.parsers.LindaStringParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class LindaParserTest {

    File tempFileOneLineRead, tempFileOneLineTake, tempFileOneLineWrite;

    LindaParser parser;

    public LindaParserTest() {
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
    public void basicReadFromFile() {
        try {
            parser = new LindaFileParser(tempFileOneLineRead.getPath());

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            LindaBasicCommand cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.READ);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void basicTakeFromFile() {
        try {
            parser = new LindaFileParser(tempFileOneLineTake.getPath());

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            LindaBasicCommand cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.TAKE);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void basicWriteFromFile() {
        try {
            parser = new LindaFileParser(tempFileOneLineWrite.getPath());

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            LindaBasicCommand cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.WRITE);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void basicReadFromString() {
        try {
            parser = new LindaStringParser("READ [ true 2 'b' ]\n" );

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            LindaBasicCommand cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.READ);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void basicTakeFromString() {
        try {
            parser = new LindaStringParser("TAKE [ true 2 'b' ]\n" );

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            LindaBasicCommand cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.TAKE);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void basicWriteFromString() {
        try {
            parser = new LindaStringParser("WRITE [ true 2 'b' ]\n" );

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            LindaBasicCommand cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.WRITE);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void multipleReadFromString() {
        try {
            parser = new LindaStringParser("READ [ true 2 'b' ]\nREAD [ 100 ]\n" );

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            LindaBasicCommand cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.READ);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

            _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.READ);
            Assert.assertEquals(cmd.getTuple(), new Tuple(100 ));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void multipleTakeFromString() {
        try {
            parser = new LindaStringParser("TAKE [ true 2 'b' ]\nTAKE [ 100 ]\n" );

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            LindaBasicCommand cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.TAKE);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

            _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.TAKE);
            Assert.assertEquals(cmd.getTuple(), new Tuple( 100 ));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void multipleWriteFromString() {
        try {
            parser = new LindaStringParser("WRITE [ true 2 'b' ]\nWRITE [ 100 ]\n" );

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            LindaBasicCommand cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.WRITE);
            Assert.assertEquals(cmd.getTuple(), new Tuple(true, 2, 'b'));

            _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            cmd = (LindaBasicCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.WRITE);
            Assert.assertEquals(cmd.getTuple(), new Tuple( 100 ));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void basicEventRegisterFromString() {
        try {
            parser = new LindaStringParser("EVENT_REGISTER READ IMMEDIATE [ 100 ] \"linda.callbacks.HelloCallback\"");

            var _cmd = parser.next();

            Assert.assertTrue( _cmd.isPresent() );

            LindaEventRegisterCommand cmd = (LindaEventRegisterCommand) _cmd.get();

            Assert.assertEquals(cmd.getOperation(), LindaOperation.EVENT_REGISTER);
            Assert.assertEquals(cmd.getMode(), Linda.eventMode.READ);
            Assert.assertEquals(cmd.getTiming(), Linda.eventTiming.IMMEDIATE);
            Assert.assertEquals(cmd.getTemplate(), new Tuple(100));
            Assert.assertEquals(cmd.getCallbackClass(), HelloCallback.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
