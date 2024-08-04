> [!IMPORTANT]  
> O projeto ainda está em desenvolvimento e não está pronto para uso.

Nheengatu é uma linguagem de programação baseada em português e inspirada na linguagem de programação Portiguol e Lua.

O objetivo dessa linguagem é ser uma linguagem de programação simples e fácil de aprender, com uma sintaxe baseada em português, para que pessoas que não falam inglês possam aprender a programar.

## Exemplos

### Olá mundo!
```nheengatu
escreva("Olá, mundo!")
```

### Soma de dois valores

```nheengatu
a := ler_numero("Digite o primeiro número: ")
b := ler_numero("Digite o segundo número: ")
soma := a + b
escreva("A soma dos dois números é: {} ", soma)
```


### Classificar idade
 ```nheengatu
idade := ler_numero("Digite a idade: ") 
se (idade < 18)
   escreva("Menor de idade")
senão 
   escreva("Maior de idade")
fim
 ```