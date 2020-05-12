import pygame

class TextButton:
    def __init__(self, center_x, center_y, font_name, font_size, text, color):
        self.center_x = center_x
        self.center_y = center_y
        self.font = pygame.font.SysFont(font_name, font_size)
        self.text = self.font.render(text, True, color)
        self.text_rect = self.text.get_rect()

    def cursor_hover(self):
        # Zwraca True lub False w zależności od tego czy kursor znajduje się nad tekstem
        mpx, mpy = pygame.mouse.get_pos() #get_pos !!!!!!!!!1
        if self.text_rect.right > mpx > self.text_rect.left and self.text_rect.top < mpy < self.text_rect.bottom:
            return True
        else:
            return False

    def update(self, text, color):
        self.text = self.font.render(text,True,color)

    def draw(self,win):
        self.text_rect.center = (self.center_x, self.center_y)
        win.blit(self.text, self.text_rect)
