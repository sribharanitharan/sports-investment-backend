# Code Citations

## License: unknown
https://github.com/tomab23/PokemonPokedex/tree/8777dd56ed4837c4968073adc8e8904a0e4c9cd0/pokedex-back/src/main/java/com/example/pokedex/security/jwt/JwtProvider.java

```
token) {
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();

    return claims.getSubject();
}

public boolean validateToken(String authToken) {
    try {
        Jwts.parserBuilder()
```


## License: unknown
https://github.com/tule-steve/CanU/tree/a607a07d13b2d68e352c845297669160f3b98653/OAuth2/src/main/java/com/canu/security/config/TokenProvider.java

```
.build()
            .parseClaimsJws(token)
            .getBody();

    return claims.getSubject();
}

public boolean validateToken(String authToken) {
    try {
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(authToken);
        return
```

