# DyslexReader

DyslexReader é um aplicativo desenvolvido para Android, com o objetivo de melhorar a experiencia de leitura para leitores disléxicos.
O projeto foi feito para a disciplina SCC0225 - Laboratório de desenvolvimento de aplicações para dispositivos móveis.

## Entrada

* Colar texto da área de transferencia (copy-paste)
* Ler texto da camera
* Copiar texto de uma página web (URL)

## Configurações

* White noise - melhora a concentração em ambientes barulhentos (ON/OFF)
* Diferencia primeira e ultima letra da palavra atual (ON/OFF)
* Seleção de tema e customização das cores
* Tempo de atraso que o botão play deve ser pressionado para que as palavras avancem automaticamente (décimos de segundos)

## Modos de leitura

* Highlight - palavra atual em cor diferenciada
* Palavra por palavra - apenas a palavra atual é mostrada

## Análise de Palavra

Pega a divisão em sílabas, pronuncia, definições e áudio para uma palavra específica.
Os dados são obtidos através de chamadas à [Wordnik API](https://developer.wordnik.com/).
Atualmente está disponível apenas dicionário em inglês.