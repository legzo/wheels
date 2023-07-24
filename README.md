# Fip Recorder

## Package
```
./gradlew shadowJar
```

## Icônes SVG

- https://iconsvg.xyz/

## Fly.io

### Connexion database

Pour la DB, il faut se connecter sur la machine applicative en ssh :

```bash
fly ssh console -a wheels
```

Puis récupérer la chaîne de connexion avec la commande 

```bash
echo $DATABASE_URL
```

En réponse, on a le user, le pass et le nom de la machine de db :

```bash
-bash-4.2# echo $DATABASE_URL
postgres://wheels_db_888leuserestici8000:dsdBNlepassesticiQddhS@wheels-db.flycast:5432/wheels?sslmode=disable
```

Moi, j'ai mis les infos de DB dans 3 vars d'env : DB_USER, DB_PASSWORD et DB_URL parce que j'avais des soucis pour 
utiliser la chaîne de connexion fournie par Fly. Donc il faut reconstituer les 3 variables, en faisant attention à bien
reprendre le paramètre `sslmode=disable`.

Pour que les inserts en mode batch soient plus performants on peut aussi mettre : `&reWriteBatchedInserts=true`