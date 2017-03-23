# spegic

Turn core.logic relations into clojure.spec specs.

Runs relations to find success values as output for generators.

## Usage

```
user=> (require '[clojure.spec :as s])
nil
user=> (require '[clojure.core.logic :as logic])
nil
user=> (require '[spegic.core :as spegic])
nil
user=> (s/def ::nil (spegic/spec logic/nilo))
:user/nil
user=> (s/conform ::nil nil)
nil
user=> (s/conform ::nil true)
:clojure.spec/invalid
user=> (s/describe ::nil)
nilo
user=> (s/explain ::nil true)
val: true fails spec: :user/nil predicate: nilo
nil
user=> (require '[clojure.test.check.generators :as gen])
nil
user=> (gen/generate (s/gen ::nil))
nil
```

## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
