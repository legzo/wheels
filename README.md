# Wheels O-O

## TODO

- [ ] Ajouter dans le line chart annuel la comparaison avec l'année n-1
- [ ] Pourcentage sur objectif annuel
- [ ] Switch entre heatmap totale / sur 4 ans / sur 1 an
- [ ] Ajouter la date de modification sur les route
- [ ] Ajouter le next Eddington number
- [x] Navigation entre "activités" et "routes"
- [x] Faire un lien vers les activités Strava
- [x] Faire un lien vers les routes sur Brouter
- [x] Dessiner les routes brouter sur un leaflet avec le plugin gpx 
- [ ] Ajouter le profil d'élévation aux activités
- [ ] Ajouter le profil d'élévation aux routes

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

## Authent Google

1. Récupérer un code en lançant cette requête dans un navigateur : https://accounts.google.com/o/oauth2/auth?client_id=xxxxx20pkcd3.apps.googleusercontent.com&redirect_uri=http://localhost:5000&scope=https://www.googleapis.com/auth/drive&response_type=code&approval_prompt=force&access_type=offline
2. Après acceptation des scopes, la page va rediriger sur localhost avec dans l'url un paramètre `code` à récupérer
3. Utiliser ce code dans la requête suivante :
    ```bash
    curl \
             -d "client_id=xxxxxn20pkcd3.apps.googleusercontent.com" \
             -d "client_secret=xxxxx" \
             -d "redirect_uri=http://localhost:5000" \
             -d "grant_type=authorization_code" \
             -d "code={{le_code_récupéré}}" \
             "https://www.googleapis.com/oauth2/v4/token"
    ``` 
4. Cette requête retourne un objet json contenant un champ `refresh_token`, à positionner ensuite comme variable 
   d'environnement, à la fois sur le launcher intellij et en variable d'env sur Fly.io.
