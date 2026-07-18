CREATE TABLE vault_words (
                             id_word SERIAL,
                             tx_word VARCHAR(100) NOT NULL,
                             PRIMARY KEY (id_word)
);

CREATE INDEX idx_vault_words_tx_word ON vault_words USING btree (tx_word);

INSERT INTO vault_words (tx_word) VALUES
  ('abacate'), ('abelha'), ('abismo'), ('abrigo'), ('agulha'), ('alface'),
  ('algodao'), ('alicate'), ('alimento'), ('aljava'), ('almofada'), ('aluno'),
  ('amora'), ('anel'), ('anjo'), ('antena'), ('arado'), ('aranha'),
  ('arco'), ('areia'), ('arvore'), ('asa'), ('asfalto'), ('astro'),
  ('atleta'), ('ave'), ('aviao'), ('azeite'), ('azul'), ('bacia'),
  ('balao'), ('banana'), ('banco'), ('bandeja'), ('barco'), ('barro'),
  ('batata'), ('bebida'), ('beijo'), ('berco'), ('bicicleta'), ('bloco'),
  ('bola'), ('bolsa'), ('boneca'), ('bosque'), ('botao'), ('braco'),
  ('brasa'), ('brilho'), ('broto'), ('cabana'), ('cabo'), ('cabra'),
  ('cacau'), ('cadeado'), ('cadeira'), ('caderno'), ('caixa'), ('cajado'),
  ('cama'), ('camelo'), ('camisa'), ('campo'), ('caneca'), ('caneta'),
  ('canoa'), ('capa'), ('capim'), ('carro'), ('carta'), ('casa'),
  ('castelo'), ('castor'), ('cavalo'), ('cebola'), ('cedro'), ('cela'),
  ('cenoura'), ('ceramica'), ('cereja'), ('chale'), ('chave'), ('chinelo'),
  ('chuva'), ('cidade'), ('cimento'), ('cinema'), ('circulo'), ('claridade'),
  ('cobra'), ('coelho'), ('colher'), ('colina'), ('coluna'), ('cometa'),
  ('computador'), ('concha'), ('corda'), ('coroa'), ('coruja'), ('costela'),
  ('couro'), ('cravo'), ('cristal'), ('cubo'), ('cultura'), ('dado'),
  ('dedo'), ('deserto'), ('diamante'), ('dinheiro'), ('disco'), ('dragao'),
  ('edificio'), ('elefante'), ('embaixada'), ('enxada'), ('escada'), ('escola'),
  ('escova'), ('espada'), ('espelho'), ('espuma'), ('estrela'), ('farol'),
  ('ferradura'), ('ferro'), ('figura'), ('fila'), ('fio'), ('flor'),
  ('floresta'), ('foca'), ('folha'), ('fonte'), ('forno'), ('fossa'),
  ('frasco'), ('fruta'), ('futuro'), ('gaiola'), ('galho'), ('galinha'),
  ('garfo'), ('garrafa'), ('gato'), ('gema'), ('geleia'), ('gelo'),
  ('girafa'), ('globo'), ('golfinho'), ('grama'), ('grilo'), ('grupo'),
  ('guitarra'), ('hamburguer'), ('helice'), ('horta'), ('hospital'), ('hotel'),
  ('ilha'), ('imagem'), ('impressora'), ('janela'), ('jardim'), ('jarra'),
  ('joelho'), ('jornal'), ('jumento'), ('lago'), ('lama'), ('lampada'),
  ('laranja'), ('lata'), ('leao'), ('leite'), ('lenha'), ('lente'),
  ('livro'), ('lobo'), ('lousa'), ('lua'), ('lupa'), ('madeira'),
  ('mala'), ('manga'), ('mapa'), ('martelo'), ('mel'), ('mesa'),
  ('milho'), ('mochila'), ('montanha'), ('morcego'), ('moto'), ('muralha'),
  ('musica'), ('navio'), ('neblina'), ('ninho'), ('nuvem'), ('oceano'),
  ('olho'), ('onda'), ('ovelha'), ('ovo');