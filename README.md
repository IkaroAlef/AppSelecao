# AppSelecao
App para Android que mostra informações de clima ao selecionar uma cidade numa lista formada pelas 15 cidades mais próximas de um marcador posicionado no Google Maps. Também inclui testes de anúncio Interstitial.

Fluxo:

1. Com um toque longo em um ponto do mapa, posicionar um marcador personalizado;
2. Tocar no botão "Buscar", localizado no canto inferior esquerdo e aguardar a lista ser exibida;
3. Tocar no nome da cidade desejada;
4. Uma tela será exibida contendo o Nome da Cidade, Temperaturas máxima e mínima e uma breve descrição do tempo atual na cidade selecionada.

extra: ao voltar da tela de informações de clima para a lista, um anúncio do tipo interstitial é exibido.
obs.: para exibir corretamente o anúncio (pois está em modo de testes), definir o ID do dispositivo (parâmetro do método options.setDevelopmentDevices(...)) na linha 84 do arquivo ActPrincipal.java. Caso não saiba o ID, basta deixar o parâmetro desse método como string vazia e executar o app. Ao tentar exibir o anúncio, será exibido na aba "Run" do Android Studio o método com o parâmetro correto do seu dispositivo android, basta copiar e colar no editor.

Testado em Motorola Moto G4 Plus (Android 6.0) e Asus Zenfone 3 Max 5.5" (Android 6.0).
