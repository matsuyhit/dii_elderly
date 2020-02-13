# coding=utf-8
from tcp_server import *

if __name__ == '__main__':

    # ドメイン名、もしくはIPアドレス。
    # ドメイン名は socket.gethostname() で取得することもできる。
    host = "192.168.207.117"

    # wellknownと衝突しない適当なポート番号
    port = 55555

    connection = TcpServer(host, port)

    try:
        while True:
            data = connection.recv_str()

            if data == 'Activity_requested_1':
                print("Activity_requested_1")
                # 行いたい何かしらのアクション (ex LEDの点灯やサーボの動作などなど）
                connection.send_str("SEND_MESSAGE")
                print("sent message")

            elif data == 'Activity_requested_2':
                print("Activity_requested_2")
                connection.send_str(b"SENDMESSAGE")
                print("sent message2")
                # 行いたい何かしらのアクション (ex LEDの点灯やサーボの動作などなど）

            elif data == 'ON_CLICK_BUTTON_QUIT':
                print("ON_CLICK_BUTTON_QUIT")
                quit()

    finally:
        connection.close()
