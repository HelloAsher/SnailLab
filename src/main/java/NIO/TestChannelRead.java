package NIO;

import java.io.FileInputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Asher on 2016/11/23.
 * 使用NIO读取数据
 * 在前面我们说过，任何时候读取数据，都不是直接从通道读取，而是从通道读取到缓冲区。所以使用NIO读取数据可以分为下面三个步骤：
 * 1. 从FileInputStream获取Channel
 * 2. 创建Buffer
 * 3. 将数据从Channel读取到Buffer中
 * 下面是一个简单的使用NIO从文件中读取数据的例子：
 */
public class TestChannelRead {
    public static void main(String[] args) throws Exception {
        URL url = TestChannelRead.class.getClassLoader().getResource("test1.txt");
        FileInputStream fin = new FileInputStream(url.getFile());

        //FileInputStream fin = new FileInputStream("test1.txt");

        // 获取通道
        FileChannel fc = fin.getChannel();

        // 创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(100);

        // 读取数据到缓冲区
        fc.read(buffer);

        buffer.flip();

        while (buffer.remaining() > 0) {
            byte b = buffer.get();
            System.out.print(((char) b));
        }

        fin.close();
    }
}
