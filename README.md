# UrbanApp Solutions

Sistema para registrar e acompanhar solicitações de serviços urbanos, suas prioridades e soluções.

## Cadastro e acesso

As contas (nome e e-mail) são armazenadas no banco H2 em arquivo (`data/cidade_atende`), para que permaneçam disponíveis após reiniciar a aplicação. As senhas são armazenadas com hash e nunca em texto simples.

Para criar uma conta, use um e-mail válido e uma senha com pelo menos 8 caracteres, incluindo uma letra maiúscula, uma minúscula e um caractere especial. Senhas com espaços ou que correspondam a senhas comuns não são aceitas.

## Painel administrativo

O primeiro administrador é criado uma única vez por meio das variáveis de ambiente `ADMIN_EMAIL` e `ADMIN_PASSWORD`. O e-mail padrão é `admin@urbanapp.local`; a senha deve atender à mesma política do cadastro. O painel fica em `/adm` e só pode ser acessado por contas com perfil `ADMIN`.

No PowerShell, defina essas variáveis antes de iniciar a aplicação:

```powershell
$env:ADMIN_EMAIL = 'admin@urbanapp.local'
$env:ADMIN_PASSWORD = '<defina-uma-senha-forte>'
.\mvnw.cmd spring-boot:run
```

O cadastro público cria somente contas `USER`. O administrador pode consultar solicitações abertas, fechá-las ou excluí-las.
