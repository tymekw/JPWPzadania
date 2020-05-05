import _pickle as pickle  # for faster serialization
import math
import random
from _thread import *
from socket import *

from player import Player, Ball

'''
W tym pliku należy uzupełnić pętlę while (linijka 153)
Należy również zmodyfikować adres IP w zmiennej host na swój z sieci lokalnej
'''

HOST = "192.168.0.107"
PORT = 8999

s = socket(AF_INET, SOCK_STREAM)
s.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)

try:
    s.bind((HOST, PORT))
except error as e:
    print(str(e))
try:
    s.listen(4)  # max. 4 users in queue
except error as e:
    print(str(e))
print("Waiting for a connection")

# GAME VARIABLES
players = list()
balls = list()
connections = 0
my_id = 0
resolution = (1280, 720)


def add_player(id):
    players.append(Player(random.randint(10, resolution[0] - 10), random.randint(10, resolution[1] - 10), 5,
                          (random.randint(0, 255), random.randint(0, 255), random.randint(254, 255)), id))


def remove_player(id):
    players[id].is_active = False
    players[id].x = 0
    players[id].y = 0
    players[id].radius = 0


def create_balls(n):
    for i in range(n):
        ball = Ball(random.randint(3, resolution[0] - 10), random.randint(3, resolution[1] - 10), 2,
                    (random.randint(0, 255), random.randint(0, 255), random.randint(0, 255)))
        balls.append(ball)


def balls_collision(players, balls):
    for player in players:
        if player.is_active == True:
            x = player.x
            y = player.y
            r = player.radius

            for ball in balls:
                ball_x = ball.x
                ball_y = ball.y

                distance = math.sqrt((x - ball_x) ** 2 + (y - ball_y) ** 2)
                if distance < r:
                    balls.remove(ball)
                    player.grow(1, resolution[1])


def players_collision(players):
    i = 0
    for player1 in players:
        if player1.is_active == True:
            i += 1
            for player2 in players[i:]:
                if player2.is_active == True:
                    distance = math.sqrt((player1.x - player2.x) ** 2 + (player1.y - player2.y) ** 2)
                    if distance < abs(player1.radius - player2.radius):

                        if player1.radius > player2.radius:
                            player1.grow(player2.radius, resolution[1])
                            player2.radius = 0
                        else:
                            player2.grow(player1.radius, resolution[1])
                            player1.radius = 0
                        break


def threaded_server():
    global balls, players
    create_balls(100)
    while True:
        balls_collision(players, balls)
        players_collision(players)
        if len(balls) < 150:
            create_balls(100)


def decrease_id(players, id):
    global connections
    for i in range(connections):
        if i >= id:
            players[id + i] = players[id + i + 1]
            # for player in players[i]:
            #     player.id -= 1


def threaded_client(connection, client_id):
    global balls, players, connections
    id = client_id
    add_player(id)

    init_data = (players, balls, id)
    connection.send(pickle.dumps(init_data))

    while True:

        try:

            data = connection.recv(2048 * 12)
            update = pickle.loads(data)

            if isinstance(update, Player):  # recv client object
                if players[id].radius == 0:
                    players[id].respawn(resolution)
                else:
                    players[id].x = update.x
                    players[id].y = update.y

            send_data = pickle.dumps((players, balls))
            connection.send(send_data)  # sending players, balls

        except Exception as e:
            print(e)
            break

    # disconneted
    try:
        print(f"Connection {id} Close")
        remove_player(id)
        connections -= 1
        connection.close()

    except Exception as e:
        print(e)


start_new_thread(threaded_server,())
while True:
    conn, addr = s.accept()
    threaded_client(conn, my_id)
    my_id += 1
    # W tej pętli serwer stale wyczekuje nowych połączeń
    # Serwer powinien akceptować takie połączenia (socket przypisany jest do zmiennej 's')
    # Następnie wywołać funkcje threded_client z gniazdem i unikalnym id klienta
    # Pro tip: Skorzystaj ze zmiennej my_id