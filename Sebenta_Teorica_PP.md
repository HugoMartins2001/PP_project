# Sebenta Teórica — Paradigmas de Programação (POO em Java)

Resumo de estudo para o exame. Cada conceito vem com a definição, o essencial para o exame e, quando dá, um exemplo do teu Trabalho Prático (PP_8230273) para fixares com código teu.

---

## 1. Os 4 pilares da Programação Orientada a Objetos

A POO assenta em quatro ideias. Se souberes explicar estas quatro com um exemplo cada, tens a base de quase todas as perguntas teóricas.

### 1.1 Encapsulamento
**O quê:** esconder os dados (atributos) dentro do objeto e só permitir acesso através de métodos controlados (getters/setters). Os atributos ficam `private`; o exterior só interage pela "porta da frente" pública.

**Porquê:** protege o estado interno (ninguém mexe num valor inválido), permite mudar a implementação por dentro sem partir o resto do código, e centraliza as validações.

**No teu trabalho:** todos os `...Impl` têm atributos `private` e acesso por getters. O `ContainerImpl.addMeasurement()` valida antes de aceitar (não deixa valor negativo, nem data mais antiga que a última) — isso é encapsulamento a trabalhar: o objeto defende a sua própria coerência.

```java
public class ContainerImpl implements Container {
    private double capacity;     // escondido
    private Measurement[] measurements;
    public boolean addMeasurement(Measurement m) throws MeasurementException {
        if (m.getValue() < 0) throw new MeasurementException("...");  // validação interna
        ...
    }
}
```

### 1.2 Herança
**O quê:** uma classe (filha) reaproveita atributos e métodos de outra (pai) com `extends`. Modela uma relação "**é um**" (um `RefrigeratedVehicle` *é um* `Vehicle`).

**Pontos-chave:** evita repetição de código; a filha pode acrescentar coisas novas e **redefinir** (override) métodos do pai; em Java só se herda de **uma** classe (herança simples). Com interfaces pode "herdar" vários contratos.

**No teu trabalho:** a herança principal está nas **interfaces** — `RefrigeratedVehicle extends Vehicle` (a interface filha acrescenta `getMaxKilometers()` ao contrato do `Vehicle`).

### 1.3 Polimorfismo
**O quê:** "muitas formas". Uma variável de um tipo geral pode apontar para objetos de tipos específicos diferentes, e a chamada de um método resolve-se para a versão certa em tempo de execução.

**Dois sabores:**
- *De subtipos / dinâmico*: `Vehicle v = new VehicleImpl(...);` — trato qualquer veículo como `Vehicle`.
- *Sobreposição (override)*: redefinir `toString()`/`equals()` na subclasse.
- (*Sobrecarga / overload*: vários métodos com o mesmo nome e parâmetros diferentes — ex.: os vários construtores do teu `VehicleImpl`.)

**Porquê é poderoso:** escreves código que funciona para tipos que ainda nem existem, desde que cumpram o contrato.

**No teu trabalho:** a `StrategyImpl.generate` percorre `Vehicle[]` e gera rotas sem saber se cada um é `VehicleImpl` ou `RefrigeratedVehicleImpl`. É polimorfismo: trabalha sobre o tipo geral `Vehicle`.

### 1.4 Abstração
**O quê:** focar no *o quê* e esconder o *como*. Expor só o essencial (a interface) e ocultar os detalhes de implementação.

**No teu trabalho:** as interfaces `Vehicle`, `AidBox`, `Route` são abstração pura — dizem o que existe (`getCode()`, `addAidBox()`), não como é feito. Os `...Impl` tratam do "como".

---

## 2. Classes abstratas vs Interfaces  *(Pergunta 1 do exame)*

| | **Interface** | **Classe abstrata** |
|---|---|---|
| Instanciável (`new`)? | Não | Não |
| Estado (atributos com valor) | Não (só constantes) | Sim |
| Construtores | Não | Sim |
| Métodos com corpo | Tradicionalmente não | Sim |
| Herança múltipla | Sim (`implements A, B`) | Não (um só `extends`) |
| Relação | "é capaz de" / contrato | "é um" + reutilização |

**Quando usar:**
- **Interface** → definir um contrato que classes (mesmo não relacionadas) cumprem; precisar de herança múltipla.
- **Classe abstrata** → várias subclasses muito parecidas que partilham estado e código comum.

**Frase-modelo:** *"Uso interface para definir um contrato e ter herança múltipla; uso classe abstrata quando subclasses partilham estado e implementação comum, aceitando a herança única."*

**No teu trabalho:** tudo são interfaces porque o enunciado deu os contratos no `.jar` e proibiu alterá-los.

---

## 3. Passagem de argumentos  *(Pergunta 2 do exame)*

**Regra de ouro: Java é SEMPRE passagem por valor (*pass-by-value*).** Nunca por referência. Muda só *o que* se copia.

- **Primitivos** (`int`, `double`...): copia-se o **valor**. Alterar o parâmetro não muda o original.
- **Objetos**: copia-se a **referência** (a "seta"). A cópia aponta para o mesmo objeto. Logo:
  - **Mutação** (chamar setter/método que muda o objeto) → **vê-se fora**.
  - **Reatribuição** (`x = new ...`) → **fica presa no método**, o original não muda.

**Equívoco a evitar:** dizer "objetos passam-se por referência". Errado — passa-se uma *cópia da referência*.

**Mnemónica:** *mutação sai, reatribuição fica.*

**No teu trabalho:** `InstitutionImpl.getVehicles()` devolve **cópias novas** (`new VehicleImpl(...)`) — cópia defensiva, para que ninguém de fora altere o estado dos veículos internos. O mesmo com `getAidBoxes()` (`Arrays.copyOf`) e `getRoute()` (`clone()`).

---

## 4. Casting, herança e polimorfismo  *(Pergunta 3 do exame)*

- **Upcasting** (subtipo → supertipo): **implícito e seguro**. `Vehicle v = new VehicleImpl(...);`
- **Downcasting** (supertipo → subtipo): **explícito e arriscado**. `VehicleImpl vi = (VehicleImpl) v;` — se o objeto não for mesmo desse tipo, lança **`ClassCastException`**.
- **Proteção:** verificar com `instanceof` antes de descer.

```java
if (v instanceof VehicleImpl) {
    VehicleImpl vi = (VehicleImpl) v;
    vi.getStatus();   // método que só existe no Impl, não na interface
}
```

**Quando fazes downcast:** quando precisas de algo específico da subclasse que o contrato geral não mostra.

**No teu trabalho:** `if (v instanceof VehicleImpl && ((VehicleImpl) v).getStatus())` na `StrategyImpl`; e `(ReportImpl) report` na `RouteGeneratorImpl`.

---

## 5. Identidade vs Igualdade; `==`, `equals()`, `toString()`  *(Pergunta 4 do exame)*

- **`==`** → **identidade**: mesmo objeto na memória (compara referências).
- **`equals()`** → **igualdade lógica**: mesmo valor segundo a regra da classe. Por defeito faz o mesmo que `==`; **redefine-se** para dar significado.
- **`toString()`** → representação textual; chamado automaticamente por `println` e na concatenação com String.

**Padrão correto de `equals()` (o teu):**
```java
@Override
public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (obj.getClass() != this.getClass()) return false;
    VehicleImpl other = (VehicleImpl) obj;
    return this.code.equals(other.code);   // regra: igual se mesmo código
}
```
**Regra de boa prática:** quem redefine `equals()` deve redefinir `hashCode()` (objetos iguais → mesmo hashCode).

**No teu trabalho:** `equals()` por código em `VehicleImpl`, `AidBoxImpl`, `ContainerImpl` — é o que deteta duplicados nos `add...`.

---

## 6. Tópicos vizinhos que costumam cair

### 6.1 Exceções
- **O quê:** mecanismo para sinalizar e tratar erros sem partir o programa. Lançam-se com `throw`, declaram-se com `throws`, apanham-se com `try/catch`.
- **Checked vs unchecked:** *checked* (ex.: `IOException`, e as tuas `RouteException`, `VehicleException`) — obrigam a `try/catch` ou `throws`; *unchecked* (`RuntimeException`, ex.: `ClassCastException`, `NullPointerException`, `InputMismatchException`) — não obrigam.
- **No teu trabalho:** criaste exceções próprias (`AidBoxException`, `ContainerException`, `MeasurementException`, `RouteException`, `VehicleException`, `PickingMapException`) e tratas o `InputMismatchException` nos menus (`MenuOptions`) quando o utilizador escreve algo inválido.

```java
try {
    option = input.nextInt();
} catch (InputMismatchException e) {
    System.out.println("Invalid option!!!");
    input.next();
}
```

### 6.2 Enumerados (`enum`)
- **O quê:** tipo com um conjunto fixo e finito de valores constantes. Mais seguro que usar `int`/`String` "à mão".
- **No teu trabalho:** `ItemType { CLOTHING, MEDICINE, NON_PERISHABLE_FOOD, PERISHABLE_FOOD }`, com método próprio. No exame, o estado `ENABLED/DISABLED` do `RefrigeratedVehicle` modela-se bem com um enum.

### 6.3 Clonagem (`clone()` / `Cloneable`)
- **O quê:** criar uma cópia de um objeto. **Cópia rasa (shallow)** copia as referências internas; **cópia profunda (deep)** copia também os objetos apontados.
- **No teu trabalho:** `AidBoxImpl implements Cloneable` e o `getRoute()` devolve clones das caixas — cópia defensiva para não expor os objetos internos da rota.

### 6.4 Arrays dinâmicos "à mão" (sem Collections)
- **Porquê:** o enunciado proibiu a Java Collections Framework (sem `ArrayList`, `HashSet`...). A solução é o padrão **array + contador + expansão**.
- **Padrão (repete-se em todo o teu código):**
```java
private Container[] containers;
private int containerCounter;
// ao adicionar:
if (containerCounter == containers.length) expand();   // duplica o array
containers[containerCounter++] = novo;
// expand():
Container[] novoArr = new Container[containers.length * 2];
for (int i = 0; i < containers.length; i++) novoArr[i] = containers[i];
containers = novoArr;
```
- **Devolver sem "lixo":** `Arrays.copyOf(arr, counter)` para não devolver as posições nulas do fim.

### 6.5 Modificadores e palavras-chave úteis
- **`private/protected/public`** — níveis de acesso (encapsulamento).
- **`static`** — pertence à classe, não à instância (ex.: `Save.SaveErrorToFile(...)`, constantes `INITIAL_SIZE`).
- **`final`** — valor que não muda depois de atribuído (constantes; atributos imutáveis).
- **`@Override`** — marca que estás a redefinir um método do pai/interface (o compilador verifica).

---

## 7. Glossário relâmpago

| Termo | Em uma linha |
|---|---|
| Classe | Molde a partir do qual se criam objetos |
| Objeto / instância | Algo concreto criado de uma classe (`new`) |
| Interface | Contrato de métodos, sem implementação |
| Classe abstrata | Base não-instanciável com estado e código parcial |
| Encapsulamento | Esconder dados, expor métodos controlados |
| Herança | Reutilizar de um pai ("é um"), `extends` |
| Polimorfismo | Mesmo tipo geral, comportamentos diferentes |
| Override | Redefinir um método herdado |
| Overload | Mesmo nome, parâmetros diferentes |
| Upcast | Subtipo → supertipo (seguro) |
| Downcast | Supertipo → subtipo (precisa de `instanceof`) |
| `==` | Mesmo objeto (identidade) |
| `equals()` | Mesmo valor (igualdade lógica) |
| Exceção checked | Obriga a `try/catch`/`throws` |
| Exceção unchecked | Não obriga (`RuntimeException`) |

---

### Como usar esta sebenta
1. Lê de cima a baixo uma vez.
2. Para cada conceito, abre o ficheiro do teu projeto que o demonstra e confirma que reconheces o padrão.
3. Tapa a coluna direita das tabelas e tenta dizer a definição de memória.
4. Para a Parte 2 (código), usa o outro ficheiro: `Guia_Estudo_Exame_PP.md`.
