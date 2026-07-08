# Guia de Estudo — Exame de Paradigmas de Programação

**Época Normal · 2025/2026 · 15-06-2026 · Duração 2h**

Este guia tem respostas-modelo a todas as perguntas do exame, ligadas ao código do teu Trabalho Prático (PP_8230273). Onde fizer sentido, aponto a classe do projeto onde o conceito já aparece, para fixares com exemplos teus.

---

# Parte 1 — Teoria

## Pergunta 1 (1,5 val) — Classes abstratas vs Interfaces

**O que são.**
Uma **classe abstrata** é uma classe que não pode ser instanciada (`new`), serve de base a outras. Pode ter atributos com estado, construtores, métodos concretos (com corpo) e métodos abstratos (sem corpo, que as subclasses são obrigadas a implementar). Uma subclasse liga-se a ela com `extends` e só pode estender **uma** classe.

Uma **interface** é um contrato: lista métodos que uma classe se compromete a implementar. Tradicionalmente só tem assinaturas de métodos (públicos e abstratos) e constantes (`public static final`). Uma classe liga-se com `implements` e pode implementar **várias** interfaces.

**Diferenças principais:**

| Aspeto | Classe abstrata | Interface |
|---|---|---|
| Instanciável | Não | Não |
| Herança múltipla | Não (só 1 `extends`) | Sim (vários `implements`) |
| Estado (atributos com valor) | Sim | Não (só constantes) |
| Construtores | Sim | Não |
| Métodos com corpo | Sim | Tradicionalmente não |
| Relação | "é um" + partilha de implementação | "é capaz de" / contrato |

**Quando usar cada uma.**
- **Classe abstrata** quando há uma relação forte "é-um" e queres **partilhar código/estado** entre subclasses muito parecidas (ex.: uma base `VehicleBase` com `code`, `capacity` e `equals()` já implementados, herdados por vários tipos de veículo).
- **Interface** quando só queres **definir um contrato** que classes possivelmente não relacionadas cumprem, ou quando precisas de "herança múltipla" de comportamento.

**Exemplo prático (ligado ao teu trabalho).**
No teu projeto tudo assenta em **interfaces**: `Vehicle`, `AidBox`, `Container`, `Route`, `Strategy`, `Institution`. Tu implementas cada uma num `...Impl` (`VehicleImpl implements Vehicle`). Isto foi a escolha certa porque o enunciado dá os contratos e proíbe alterá-los — as interfaces definem *o quê*, os `Impl` definem *o como*.

```java
// CONTRATO (interface): diz o que um veículo sabe fazer
public interface Vehicle {
    String getCode();
    ItemType getSupplyType();
    double getMaxCapacity();
}

// CLASSE ABSTRATA: partilharia estado/código comum entre veículos
public abstract class VehicleBase implements Vehicle {
    protected String code;                 // estado partilhado
    public String getCode() { return code; } // método concreto herdado
    public abstract double getMaxCapacity(); // cada subclasse decide
}
```

Frase para o exame: *"A interface foca-se no contrato e permite herança múltipla; a classe abstrata foca-se em reutilização de estado e código comum, mas só permite uma herança. Uso interface para definir capacidades; uso classe abstrata quando várias subclasses partilham implementação."*

---

## Pergunta 2 (1,5 val) — Passagem de argumentos em Java

**Ideia central: Java é SEMPRE *pass-by-value* (passagem por valor).** O que muda é *o que* é esse valor.

**Tipos primitivos** (`int`, `double`, `boolean`, ...): passa-se uma **cópia do valor**. Alterar o parâmetro dentro do método **não** afeta a variável original.

```java
void incrementa(int x) { x = x + 1; }   // mexe na cópia
int a = 5;
incrementa(a);
System.out.println(a); // 5  -> não mudou
```

**Referências de objetos**: passa-se uma **cópia da referência** (uma cópia do "endereço"), não o objeto. Consequências:
- Podes **alterar o estado** do objeto apontado (chamar setters/métodos) e isso **vê-se fora**, porque a cópia da referência aponta para o mesmo objeto.
- Se **reatribuíres** o parâmetro a um objeto novo (`obj = new ...`), isso **não** afeta a variável original — só mudaste a cópia local da referência.

```java
void desliga(VehicleImpl v) { v.setStatus(false); }  // muda o ESTADO -> vê-se fora
void troca(VehicleImpl v) { v = new VehicleImpl("X", ItemType.MEDICINE); } // reatribui -> NÃO se vê fora
```

**Equívoco frequente.** Dizer que "objetos são passados por referência". **Errado.** Java passa uma *cópia da referência*. Por isso `desliga()` funciona (mexe no objeto) mas `troca()` não (só troca a cópia local).

**Ligação ao teu código.** Repara que em `InstitutionImpl.getVehicles()` tu **não** devolves os veículos originais — crias `new VehicleImpl(...)` cópias. Isso é uma **cópia defensiva**: precisamente para evitar que quem recebe a referência consiga alterar o estado dos veículos internos da instituição. O mesmo padrão aparece em `getAidBoxes()` / `getRoute()` com `Arrays.copyOf` e `clone()`. Bom exemplo concreto para a resposta.

---

## Pergunta 3 (1,5 val) — Casting no contexto de herança e polimorfismo

**Polimorfismo:** uma variável de tipo "pai" (interface/superclasse) pode apontar para um objeto de tipo "filho". Ex.: `Vehicle v = new VehicleImpl(...);`. Pela variável `v` só vês os métodos do contrato `Vehicle`.

**Upcasting (alargar → seguro, implícito).** Converter de subtipo para supertipo. É sempre seguro, não precisa de sintaxe especial.
```java
Vehicle v = new VehicleImpl("V1", ItemType.MEDICINE); // VehicleImpl -> Vehicle (upcast implícito)
```

**Downcasting (estreitar → arriscado, explícito).** Converter de supertipo para subtipo. Tens de o escrever `(Tipo)` e só é válido se o objeto **for mesmo** desse tipo em tempo de execução. Caso contrário lança `ClassCastException`.
```java
Vehicle v = ...;
VehicleImpl vi = (VehicleImpl) v;   // só funciona se v for mesmo um VehicleImpl
vi.getStatus();                     // método que só existe no Impl
```

**Risco e como prevenir.** Um downcast errado rebenta em runtime (`ClassCastException`). Proteges-te com `instanceof` antes de converter:
```java
if (v instanceof VehicleImpl) {
    VehicleImpl vi = (VehicleImpl) v;
    ...
}
```

**Ligação ao teu código.** Fazes este padrão em vários sítios:
- Em `StrategyImpl.generate`: `if (v instanceof VehicleImpl && ((VehicleImpl) v).getStatus())` — usas `instanceof` antes do cast para chamar `getStatus()`, que **não** está na interface `Vehicle`, só no `VehicleImpl`.
- Em `RouteGeneratorImpl`: `if (report instanceof ReportImpl) { ReportImpl r = (ReportImpl) report; ... }` para aceder aos setters específicos.

Frase para o exame: *"O upcasting é seguro e implícito (subtipo → supertipo). O downcasting é explícito e arriscado porque pode lançar ClassCastException; deve ser precedido de instanceof. Uso downcasting quando preciso de funcionalidade específica da subclasse que o contrato do supertipo não expõe — como getStatus() no VehicleImpl."*

---

## Pergunta 4 (1,5 val) — Identidade vs Igualdade; == vs equals(); toString()

**Identidade (`==`)** compara **referências**: pergunta "são o *mesmo* objeto na memória?". Para primitivos compara valores; para objetos compara endereços.

**Igualdade (`equals()`)** compara **conteúdo lógico**: pergunta "representam o *mesmo* valor segundo a regra da classe?". Por defeito (herdado de `Object`) `equals()` faz o mesmo que `==`, por isso **redefine-se** para dar significado próprio.

```java
VehicleImpl a = new VehicleImpl("V1", ItemType.MEDICINE);
VehicleImpl b = new VehicleImpl("V1", ItemType.MEDICINE);
a == b;        // false -> objetos diferentes na memória
a.equals(b);   // true  -> mesmo código "V1" (pela tua regra)
```

**toString()** devolve a representação textual do objeto. Por defeito mostra algo como `VehicleImpl@1b6d3586` (nome da classe + hashcode). Redefine-se para imprimir informação útil; é chamado automaticamente em `System.out.println(obj)` e na concatenação com `String`.

**Exemplo de redefinição correta (igual ao teu `VehicleImpl`).**
```java
@Override
public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;                       // mesmo objeto -> igual
    if (obj.getClass() != this.getClass()) return false; // tipos diferentes -> não igual
    VehicleImpl other = (VehicleImpl) obj;              // downcast seguro
    return this.code.equals(other.code);               // regra: igual se mesmo código
}

@Override
public String toString() {
    return "Code : " + code + "\nType of supply : " + supplyType
         + "\nMax capacity : " + capacity + "\n";
}
```

**Ligação ao teu código.** Tens este `equals()` (igual se mesmo `code`) em `VehicleImpl`, `AidBoxImpl` e `ContainerImpl`. E `toString()` redefinido em quase todas as classes (`InstitutionImpl`, `AidBoxImpl`, `ReportImpl`, etc.). Nota importante para defender: ao redefinir `equals()`, o ideal é também redefinir `hashCode()` (contrato: objetos iguais devem ter o mesmo hashCode) — no teu projeto não o fazes porque não usas estruturas baseadas em hash da framework (foi proibida), mas se o professor perguntar, é o complemento correto.

---

# Parte 2 — Prática

> Interfaces dadas no enunciado (usa estas assinaturas tal e qual):
> ```java
> public interface Vehicle {
>     String getCode();
>     ItemType getSupplyType();
>     double getMaxCapacity();
> }
> public interface RefrigeratedVehicle extends Vehicle {
>     double getMaxKilometers();
>     boolean equals(Object obj);
> }
> public interface Container {
>     ItemType getType();
>     double getCapacity();
>     Measurement getLastMeasurement();
> }
> public interface Measurement { double getValue(); }
> public interface AidBox { Container[] getContainers(); }
> public interface Route {
>     void addAidBox(AidBox aidBox) throws RouteException;
>     AidBox removeAidBox(AidBox aidBox) throws RouteException;
>     AidBox[] getRoute();
> }
> ```

## 1.a (3 val) — Classe `RefrigeratedVehicleImpl`

Requisitos: implementa `RefrigeratedVehicle`; tem limite máximo de km; tem um **estado (ENABLED, DISABLED)** inicializado a **ENABLED** por defeito; `equals()` considera dois veículos iguais se tiverem o **mesmo código** (`getCode()`).

Modelo o estado com um `enum` (mais legível que um boolean e bate certo com o enunciado):

```java
package com.estg.pickingManagement;

import com.estg.core.ItemType;

public class RefrigeratedVehicleImpl implements RefrigeratedVehicle {

    // Estado pedido no enunciado
    public enum State { ENABLED, DISABLED }

    private final String code;
    private final ItemType supplyType;
    private final double maxCapacity;
    private final double maxKilometers;
    private State state;

    // Construtor: estado ENABLED por defeito
    public RefrigeratedVehicleImpl(String code, ItemType supplyType,
                                   double maxCapacity, double maxKilometers) {
        this.code = code;
        this.supplyType = supplyType;
        this.maxCapacity = maxCapacity;
        this.maxKilometers = maxKilometers;
        this.state = State.ENABLED;          // <-- por defeito ENABLED
    }

    // --- Métodos da interface Vehicle ---
    @Override
    public String getCode() { return this.code; }

    @Override
    public ItemType getSupplyType() { return this.supplyType; }

    @Override
    public double getMaxCapacity() { return this.maxCapacity; }

    // --- Métodos da interface RefrigeratedVehicle ---
    @Override
    public double getMaxKilometers() { return this.maxKilometers; }

    // --- Gestão de estado ---
    public State getState() { return this.state; }
    public void setState(State state) { this.state = state; }
    public boolean isEnabled() { return this.state == State.ENABLED; }

    // --- equals: iguais se mesmo código ---
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != this.getClass()) return false;
        RefrigeratedVehicleImpl other = (RefrigeratedVehicleImpl) obj;
        return this.code.equals(other.code);
    }

    @Override
    public String toString() {
        return "RefrigeratedVehicle{code=" + code + ", supplyType=" + supplyType
             + ", maxCapacity=" + maxCapacity + ", maxKilometers=" + maxKilometers
             + ", state=" + state + "}";
    }
}
```

**Pontos que dão nota** (referi-os na defesa): estado ENABLED por defeito posto no construtor; `equals()` com as 4 guardas habituais (null, mesmo objeto, mesma classe, comparar `code`); usar `@Override`. É exatamente o mesmo padrão do teu `VehicleImpl` — a diferença é o `getMaxKilometers()` extra e o estado por `enum` em vez de `boolean`.

## 1.b (2 val) — `main` que testa cada método

Um teste por método implementado:

```java
public class TestRefrigerated {
    public static void main(String[] args) {
        RefrigeratedVehicleImpl v =
            new RefrigeratedVehicleImpl("RV1", ItemType.PERISHABLE_FOOD, 1000.0, 50.0);

        // getCode()
        System.out.println("getCode -> " + v.getCode());            // RV1
        // getSupplyType()
        System.out.println("getSupplyType -> " + v.getSupplyType()); // PERISHABLE_FOOD
        // getMaxCapacity()
        System.out.println("getMaxCapacity -> " + v.getMaxCapacity()); // 1000.0
        // getMaxKilometers()
        System.out.println("getMaxKilometers -> " + v.getMaxKilometers()); // 50.0

        // estado por defeito + alteração
        System.out.println("isEnabled (defeito) -> " + v.isEnabled()); // true
        v.setState(RefrigeratedVehicleImpl.State.DISABLED);
        System.out.println("isEnabled (após desligar) -> " + v.isEnabled()); // false

        // equals(): mesmo código -> true; código diferente -> false
        RefrigeratedVehicleImpl mesmoCodigo =
            new RefrigeratedVehicleImpl("RV1", ItemType.MEDICINE, 500.0, 30.0);
        RefrigeratedVehicleImpl outroCodigo =
            new RefrigeratedVehicleImpl("RV2", ItemType.PERISHABLE_FOOD, 1000.0, 50.0);
        System.out.println("equals mesmo codigo -> " + v.equals(mesmoCodigo)); // true
        System.out.println("equals codigo diff  -> " + v.equals(outroCodigo)); // false

        // toString()
        System.out.println("toString -> " + v);
    }
}
```

A ideia: cada `println` testa **um** método e comenta o resultado esperado. Isto é o que o teu `Main`/`MenuOptions` faz de forma interativa, mas aqui em formato de teste direto.

## 2.a (4 val) — Métodos auxiliares da geração de rota

### `hasCollectableContainer(Vehicle vehicle, AidBox aidbox)`

Devolve `true` se a caixa tiver **pelo menos um** container cujo tipo é igual ao tipo do veículo **e** cuja **última medição > 80% da capacidade**.

```java
public boolean hasCollectableContainer(Vehicle vehicle, AidBox aidbox) {
    if (vehicle == null || aidbox == null) {
        return false;
    }
    Container[] containers = aidbox.getContainers();
    if (containers == null) {
        return false;
    }
    for (Container c : containers) {
        if (c == null) {
            continue;
        }
        // 1) tipo do container igual ao tipo de item do veículo
        if (c.getType().equals(vehicle.getSupplyType())) {
            Measurement last = c.getLastMeasurement();
            // 2) última medição superior a 80% da capacidade
            if (last != null && last.getValue() > c.getCapacity() * 0.8) {
                return true;   // basta um
            }
        }
    }
    return false;
}
```

Pontos de avaliação: verificar `getType().equals(getSupplyType())`; usar `getLastMeasurement().getValue()`; comparar com `getCapacity() * 0.8` com **`>`** (estritamente superior, como diz "superior a 80%"); devolver à primeira ocorrência. *(No teu código atual usas `getMeasurements()[last].getValue()` — aqui o enunciado dá `getLastMeasurement()` diretamente, por isso usa esse.)*

### `addAidBoxToRoute(Route route, AidBox aidbox, RouteValidator validator)`

Devolve `true` se a caixa **for adicionada** à rota. Antes de adicionar, valida com `validator.validate(route, aidbox)`. Se validado, adiciona com `route.addAidBox(aidBox)`. Se o `addAidBox` lançar `RouteException`, devolve `false`.

```java
public boolean addAidBoxToRoute(Route route, AidBox aidbox, RouteValidator validator) {
    if (route == null || aidbox == null || validator == null) {
        return false;
    }
    // 1) validação prévia
    if (!validator.validate(route, aidbox)) {
        return false;
    }
    // 2) tentar adicionar; se rebentar RouteException -> false
    try {
        route.addAidBox(aidbox);
        return true;
    } catch (RouteException e) {
        return false;
    }
}
```

Pontos de avaliação: validar **primeiro** com `validator.validate(...)`; só adicionar se validado; envolver `route.addAidBox` num `try/catch (RouteException)` e devolver `false` no catch, `true` no sucesso. Isto liga ao teu `RouteValidatorImpl.validate` (verifica capacidade) e ao teu `RouteImpl.addAidBox` (lança `RouteException`).

## 2.b (5 val) — `generate` em `StrategyImpl`

Regras: para **cada veículo** de `inst.getVehicles()` cria uma **nova rota** (assume um veículo por tipo); as caixas vêm de `inst.getAidBoxes()`; usa os métodos da 2.a; o array devolvido só pode ter **rotas com caixas** (sem posições nulas e sem rotas vazias).

```java
@Override
public Route[] generate(IInstitution inst, RouteValidator validator) {
    if (inst == null) {
        return new Route[0];
    }
    Vehicle[] vehicles = inst.getVehicles();
    AidBox[] aidBoxes = inst.getAidBoxes();
    if (vehicles == null || aidBoxes == null) {
        return new Route[0];
    }

    // No máximo uma rota por veículo
    Route[] temp = new Route[vehicles.length];
    int routeCount = 0;

    for (Vehicle vehicle : vehicles) {
        if (vehicle == null) {
            continue;
        }
        Route route = new RouteImpl(vehicle);   // nova rota por veículo

        for (AidBox aidbox : aidBoxes) {
            if (aidbox == null) {
                continue;
            }
            // só interessa se a caixa tem container recolhível para este veículo
            if (hasCollectableContainer(vehicle, aidbox)) {
                // tenta adicionar (valida capacidade e trata RouteException)
                addAidBoxToRoute(route, aidbox, validator);
            }
        }

        // guardar a rota apenas se NÃO ficou vazia
        if (route.getRoute().length > 0) {
            temp[routeCount++] = route;
        }
    }

    // "compactar": devolver array sem posições nulas
    Route[] result = new Route[routeCount];
    for (int i = 0; i < routeCount; i++) {
        result[i] = temp[i];
    }
    return result;
}
```

**Como explicar a lógica** (é o coração do exame, vale 5 valores):
1. Um ciclo externo pelos **veículos** → cada um gera **uma** rota nova (`new RouteImpl(vehicle)`).
2. Um ciclo interno pelas **caixas** → para cada caixa pergunta `hasCollectableContainer` (tipo compatível + lotação > 80%).
3. Se sim, `addAidBoxToRoute` faz a validação de capacidade e adiciona (ou não, se rebentar).
4. No fim, só se guarda a rota se tiver pelo menos uma caixa (`getRoute().length > 0`) → cumpre "sem rotas vazias".
5. Copia-se para um array do tamanho exato (`routeCount`) → cumpre "sem posições nulas". *(Se pudesses usar a framework usarias `Arrays.copyOf(temp, routeCount)`, que é o que fazes no teu `StrategyImpl` real.)*

Isto é praticamente a versão "de exame" do teu `StrategyImpl.generate` — a diferença é que o enunciado já te dá os auxiliares `hasCollectableContainer` (em vez do teu teste `load > 0`) e `addAidBoxToRoute` (em vez de chamares `validator.validate` + `route.addAidBox` à mão).

---

## Resumo rápido para decorar

- **P1.1** Interface = contrato (herança múltipla, sem estado); classe abstrata = base com estado/código (herança única).
- **P1.2** Java é sempre *pass-by-value*; em objetos passa-se cópia da referência → muda-se o estado, não a referência original.
- **P1.3** Upcast seguro/implícito; downcast explícito/arriscado → usar `instanceof` (ClassCastException).
- **P1.4** `==` identidade (mesmo objeto); `equals()` igualdade lógica (redefinir); `toString()` representação textual.
- **P2.1a** `RefrigeratedVehicleImpl`: estado ENABLED por defeito, `equals()` por `getCode()`.
- **P2.2a** `hasCollectableContainer`: tipo igual + medição > 80% capacidade. `addAidBoxToRoute`: valida → adiciona → `try/catch RouteException`.
- **P2.2b** `generate`: uma rota por veículo, percorre caixas, usa os 2 auxiliares, devolve só rotas não vazias e sem nulos.

