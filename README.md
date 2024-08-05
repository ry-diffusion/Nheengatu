> [!IMPORTANT]  
> O projeto ainda está em desenvolvimento e não está pronto para uso.

Nheengatu é uma linguagem de programação baseada em português e inspirada na linguagem de programação Portiguol e Lua.

O objetivo dessa linguagem é ser uma linguagem de programação simples e fácil de aprender, com uma sintaxe baseada em português, para que pessoas que não falam inglês possam aprender a programar.

## Exemplos

### Olá mundo!
```nheengatu
pacote Principal
inicio
 imprima("Olá, mundo!")
fim
```

### Soma de dois valores

```nheengatu
a = ler_numero("Digite o primeiro número: ")
b = ler_numero("Digite o segundo número: ")
soma = a + b
imprima("A soma dos dois números é: {} ", soma)
```


### Classificar idade
 ```nheengatu
idade = ler_numero("Digite a idade: ") 
se (idade < 18)
   imprima("Menor de idade")
senão 
   imprima("Maior de idade")
fim
 ```

# Ferramentas
- JetBrains Compose: Biblioteca para criação de interfaces gráficas
- ANTLR: Ferramenta para geração de analisadores léxicos e sintáticos
- Kotlin: Linguagem de programação utilizada para implementar o compilador

# Status
- [ ] Analisador léxico
- [ ] Analisador sintático
- [ ] Analisador semântico
- [ ] Gerador de código JVM
- [ ] Uma IDE Básica
- [ ] Funcionar :O

# Como isso vai funcionar?
Na teoria o compilador vai funcionar da seguinte forma:
1. O código fonte em Nheengatu será lido pelo analisador léxico, que vai transformar o código em tokens.
2. O analisador sintático vai ler os tokens e verificar se a ordem deles está correta.
3. O analisador semântico vai verificar se as variáveis e funções estão sendo usadas corretamente.
4. O gerador de código JVM vai transformar o código em bytecode Java.
5. O bytecode Java será executado pela JVM.