# PP — Cábula p/ Apple Watch

Cartões curtos. Baseados no teu projeto (AidBox / recolha humanitária). Lê de cima para baixo.

---

## PARTE 1 — RESPOSTAS-RESUMO (conceitos POO)

### 1. Interface vs Classe
Interface = contrato (só assinaturas). Classe Impl = implementação.
No teu projeto: `AidBox` (interface) / `AidBoxImpl` (classe). Os contratos NÃO se alteram; só se implementam.

### 2. Encapsulamento
Atributos `private` + getters. Estado só muda por métodos.
Ex: `code`, `zone` são private; acesso via `getCode()`.

### 3. Cópia defensiva (importante!)
Getters de arrays devolvem CÓPIA, não o array interno.
`return Arrays.copyOf(this.containers, this.containerCounter);`
Evita que código externo altere o interior do objeto.

### 4. equals()
Redefinido para comparar por identidade lógica (o código).
Passos: null? → mesmo obj? → mesma classe (`getClass()`)? → compara `code`.
Ex: dois AidBox são iguais se tiverem o mesmo `code`.

### 5. clone() / Cloneable
`implements Cloneable` + override `clone()`.
Shallow = cópia superficial (referências partilhadas).
Deep = cópia dos objetos internos. No `AidBoxImpl.clone()` copia-se o array de containers.

### 6. Herança e polimorfismo
`RefrigeratedVehicle extends Vehicle` (enunciado): veículo refrigerado é um Vehicle especial (perecíveis + limite de km com carga).
Polimorfismo: chamar `getSupplyType()` funciona para qualquer tipo de Vehicle.

### 7. Classe abstrata vs interface
Abstrata: pode ter estado + métodos concretos; herança simples (extends).
Interface: só contrato; herança múltipla (implements várias).

### 8. Enum
`ItemType`: CLOTHING, MEDICINE, NON_PERISHABLE_FOOD, PERISHABLE_FOOD.
Conjunto fixo de constantes. Usa-se com `switch` ou `.equals()`.

### 9. Exceções personalizadas
`AidBoxException`, `ContainerException`, `MeasurementException`, `VehicleException`, `RouteException`, `PickingMapException`.
Estendem `Exception` (checked). Lançadas com `throw new X("msg")`; assinatura com `throws`.

### 10. Arrays sem Collections (regra do enunciado)
Proibido Java Collections Framework.
Padrão: array + contador + método `expand()` que duplica (`length * 2`) quando cheio.
Ex: `INITIAL_SIZE = 10`, `EXPAND = 2`.

### 11. Agregação / Composição
Institution → tem AidBoxes e Vehicles.
AidBox → tem Containers.
Container → tem Measurements.
Route → tem AidBoxes + 1 Vehicle.

### 12. Static / constante
`static final int EXPAND = 2;` — valor único, partilhado, imutável.
`static` = pertence à classe, não à instância.

### 13. Padrão Strategy
`Strategy` define o critério de geração de rotas. `StrategyImpl.generate()` decide que containers recolher. Permite trocar o algoritmo sem mudar o resto.

### 14. RouteValidator
Valida se um AidBox cabe na rota: soma cargas dos containers do tipo do veículo + nova carga ≤ `maxCapacity`.

### 15. Report
Estatísticas da geração: veículos usados/não usados, containers recolhidos/não recolhidos, distância e duração totais, data.

### 16. Importação JSON
`ImporterImpl` lê AidBoxes.json (código, zona, lat/lon, containers em kg) e Distances.json (metros / segundos; "Base" = partida e chegada). Instancia os objetos.

### 17. Simulação de sensor (requisito adicional)
Classe que gera aleatoriamente uma leitura (Measurement) dentro dos limites [0, capacidade] do container.

---

## PARTE 2 — EXEMPLOS DE EXAME (recurso)

### T1. "Distingue interface de classe abstrata."
Interface: só contrato, sem estado, `implements`, herança múltipla.
Abstrata: pode ter atributos/métodos concretos, `extends`, herança simples, pode ter construtor.

### T2. "Porque é que os getters devolvem cópias?"
Cópia defensiva → protege o encapsulamento; impede alteração externa do array interno.

### T3. "Explica o override de equals(). Precisas de hashCode()?"
equals compara por atributo-chave (code). Se usasses estruturas baseadas em hash, sim, tens de redefinir hashCode() para manter o contrato equals/hashCode.

### T4. "Shallow vs deep copy."
Shallow: copia referências (objetos internos partilhados).
Deep: copia também os objetos internos (independentes).

### T5. "O que é polimorfismo? Dá exemplo do projeto."
Mesma chamada, comportamento conforme o tipo real. Ex: array de `Vehicle` com VehicleImpl e RefrigeratedVehicle; `getSupplyType()` resolvido em runtime.

### T6. "Checked vs unchecked exception."
Checked: estende Exception, obriga try/catch ou throws (as tuas *Exception).
Unchecked: estende RuntimeException, não obriga (ex: NullPointerException).

### T7. "Como cresce um array sem Collections?"
Quando `counter == length`, cria array `length*2`, copia elementos, substitui referência. (método expand)

### T8. "Diferença entre agregação e composição."
Agregação: "tem-um" com vidas independentes.
Composição: a parte não existe sem o todo (relação mais forte).

### T9. PRÁTICO: "Implementa addContainer(...) que não permite tipos repetidos."
```java
public boolean addContainer(Container c) throws ContainerException {
    if (c == null) throw new ContainerException("null");
    if (findContainer(c) != -1) return false;          // já existe
    if (getContainer(c.getType()) != null)             // tipo repetido
        throw new ContainerException("tipo repetido");
    if (counter == containers.length) expand();
    containers[counter++] = c;
    return true;
}
```

### T10. PRÁTICO: "equals por código."
```java
@Override
public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (obj.getClass() != this.getClass()) return false;
    AidBoxImpl o = (AidBoxImpl) obj;
    return this.code.equals(o.getCode());
}
```

### T11. PRÁTICO: "Método que valida capacidade da rota."
Somar a carga (última medição) de cada container do tipo do veículo já na rota + nova; devolver `soma <= vehicle.getMaxCapacity()`.

### T12. "Regra dos veículos refrigerados."
Só perecíveis; não circular mais que um nº máximo de km COM carga.

### T13. "Vantagem de programar para interfaces (contratos)?"
Baixo acoplamento; podes trocar a implementação sem alterar quem a usa; facilita testes.

### T14. "Onde usarias uma constante static final?"
Valores fixos partilhados: fator de expansão, tamanho inicial dos arrays, R da Terra no cálculo de distância.

---

## ARMADILHAS (rever antes do exame)
- `getClass()` (não `instanceof`) no equals para exigir mesma classe exata.
- Enunciado PROÍBE Collections Framework → não uses ArrayList/HashSet/HashMap.
  (No teu StrategyImpl há `HashSet` — num exame terias de o substituir por array.)
- Exceção lançada precisa de `throws` na assinatura (checked).
- Getter de coleção → devolver cópia, nunca o array interno.
- Perecíveis: recolha imediata, independente da lotação.
