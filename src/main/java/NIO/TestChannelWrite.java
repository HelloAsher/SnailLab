package NIO;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Asher on 2016/11/23.
 * 使用NIO写入数据
 * 使用NIO写入数据与读取数据的过程类似，同样数据不是直接写入通道，而是写入缓冲区，可以分为下面三个步骤：
 * 1. 从FileOutputStream获取Channel
 * 2. 创建Buffer
 * 3. 将数据从Buffer写入到Channel中,由Channel将数据写入文件
 * 下面是一个简单的使用NIO向文件中写入数据的例子：
 */
public class TestChannelWrite {
    static private final byte message[] = {55, 55, 55, 55, 55, 66};

    public static void main(String[] args) throws Exception{
        FileOutputStream fout = new FileOutputStream("resources/test");

        FileChannel fc = fout.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        for (int i = 0; i < message.length; ++i) {
            buffer.put(message[i]);
        }

        buffer.flip();

        fc.write(buffer);

        fout.close();
    }
}
