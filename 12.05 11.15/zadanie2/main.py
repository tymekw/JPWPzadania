import pygame

pygame.init()

win = pygame.display.set_mode((800, 600), pygame.RESIZABLE)
pygame.display.set_caption(" :-) ")
bg = pygame.image.load("background.png")
bg = pygame.transform.scale(bg, (800, 600))

win.blit(bg, (0, 0))
x = 200
y = 300
vel = 5



run = True
while run:
    pygame.time.delay(100)
    keys = pygame.key.get_pressed()

    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            run = False
    keys = pygame.key.get_pressed()

    if keys[pygame.K_LEFT]:
        x -= vel

    if keys[pygame.K_RIGHT]:
        x += vel

    if keys[pygame.K_UP]:
        y -= vel

    if keys[pygame.K_DOWN]:
        y += vel

    if 370 <= x <= 430 and 330 >= y >= 270:
        win.blit(bg, (0, 0))
        pygame.draw.circle(win, (255, 255, 0), (400, 300), 15)
        pygame.draw.circle(win, (255, 0, 0), (x, y), 15)
        pygame.display.update()
    else:
        win.blit(bg, (0, 0))
        pygame.draw.circle(win, (255, 255, 0), (400, 300), 15)
        pygame.draw.circle(win, (255, 255, 255), (x, y), 15)
        pygame.display.update()

pygame.quit()

