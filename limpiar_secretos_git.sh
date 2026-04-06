#!/bin/bash
# =============================================================================
# SCRIPT PARA ELIMINAR SECRETOS DEL HISTORIAL DE GIT
# =============================================================================
# Este script limpia el token de SonarQube y otros secretos que fueron
# commiteados accidentalmente en el historial de Git.
#
# INSTRUCCIONES:
#   1. Haz un backup completo del repositorio antes de ejecutar
#   2. Asegúrate de estar en la raíz del repositorio
#   3. Informa a todos los colaboradores que deberán hacer fresh clone
#   4. Después de ejecutar, haz force push a todas las ramas
#
# REQUISITO: Tener instalado BFG Repo Cleaner o git filter-repo
#   - BFG: https://rtyley.github.io/bfg-repo-cleaner/
#   - git-filter-repo: pip install git-filter-repo
#
# USO: bash limpiar_secretos_git.sh
# =============================================================================

set -e  # Salir si cualquier comando falla

echo "=========================================="
echo "  LIMPIEZA DE SECRETOS EN HISTORIAL GIT"
echo "=========================================="
echo ""

# ──────────────────────────────────────────────
# PASO 1: Verificar que estamos en el repo
# ──────────────────────────────────────────────
if [ ! -d ".git" ]; then
    echo "❌ ERROR: No estás en la raíz del repositorio Git."
    echo "   Ejecuta este script desde: buses-venegas-S.A/"
    exit 1
fi

echo "✅ Repositorio Git detectado."
echo ""

# ──────────────────────────────────────────────
# PASO 2: Crear backup
# ──────────────────────────────────────────────
BACKUP_DIR="../buses-venegas-BACKUP-$(date +%Y%m%d-%H%M%S)"
echo "📦 Creando backup en: $BACKUP_DIR"
cp -r . "$BACKUP_DIR"
echo "✅ Backup creado."
echo ""

# ──────────────────────────────────────────────
# PASO 3: Lista de secretos a eliminar
# ──────────────────────────────────────────────
# El token de SonarQube expuesto que debe ser removido del historial
SONAR_TOKEN="SONAR_TOKEN_REVOCADO"

echo "🔍 Secretos a eliminar del historial:"
echo "   - Token SonarQube: sqp_00a1...(truncado)"
echo ""

# ──────────────────────────────────────────────
# PASO 4: Verificar que el secreto existe en el historial
# ──────────────────────────────────────────────
echo "🔍 Verificando si el secreto existe en el historial..."
if git log --all --oneline -S "$SONAR_TOKEN" -- | grep -q .; then
    echo "⚠️  El secreto FUE ENCONTRADO en el historial de Git."
    echo "   Procediendo con la limpieza..."
else
    echo "ℹ️  El secreto ya no aparece en el historial actual."
    echo "   (Puede que ya fue limpiado o no fue commiteado directamente)"
fi
echo ""

# ──────────────────────────────────────────────
# MÉTODO A: Usando git-filter-repo (RECOMENDADO)
# ──────────────────────────────────────────────
if command -v git-filter-repo &> /dev/null; then
    echo "🛠️  Usando git-filter-repo (método preferido)..."

    # Crear archivo de reemplazos
    cat > /tmp/replacements.txt << EOF
$SONAR_TOKEN==>SONAR_TOKEN_REMOVIDO
EOF

    git filter-repo \
        --replace-text /tmp/replacements.txt \
        --force

    echo "✅ git-filter-repo completado."

# ──────────────────────────────────────────────
# MÉTODO B: Usando BFG Repo Cleaner
# ──────────────────────────────────────────────
elif command -v bfg &> /dev/null || [ -f "bfg.jar" ]; then
    echo "🛠️  Usando BFG Repo Cleaner..."

    BFG_CMD="bfg"
    [ -f "bfg.jar" ] && BFG_CMD="java -jar bfg.jar"

    # Crear archivo con secretos a eliminar
    echo "$SONAR_TOKEN" > /tmp/secrets_to_remove.txt

    $BFG_CMD --replace-text /tmp/secrets_to_remove.txt --no-blob-protection

    # Limpiar el historial
    git reflog expire --expire=now --all
    git gc --prune=now --aggressive

    echo "✅ BFG Repo Cleaner completado."

# ──────────────────────────────────────────────
# MÉTODO C: Usando git filter-branch (LENTO, último recurso)
# ──────────────────────────────────────────────
else
    echo "⚠️  Ni git-filter-repo ni BFG están disponibles."
    echo "   Usando git filter-branch (más lento)..."
    echo ""

    FILTER_SCRIPT="sed -i 's/$SONAR_TOKEN/SONAR_TOKEN_REMOVIDO/g' \"\$(git rev-parse --show-toplevel)/backend/buses-api/build.gradle\" 2>/dev/null || true"

    FILTER_BRANCH_MSG="$(git filter-branch \
        --tree-filter "$FILTER_SCRIPT" \
        --tag-name-filter cat \
        -- --all 2>&1)" || true

    git reflog expire --expire=now --all
    git gc --prune=now --aggressive

    echo "✅ git filter-branch completado."
fi

echo ""

# ──────────────────────────────────────────────
# PASO 5: Instrucciones post-limpieza
# ──────────────────────────────────────────────
echo "=========================================="
echo "  PASOS FINALES OBLIGATORIOS"
echo "=========================================="
echo ""
echo "1. 🔑 REVOCAR EL TOKEN EXPUESTO INMEDIATAMENTE:"
echo "   - Ve a: http://localhost:9000 (o tu servidor SonarQube)"
echo "   - Settings > Security > Tokens"
echo "   - Busca y REVOCA el token: sqp_00a1901f...(primeros caracteres)"
echo "   - Genera un nuevo token y guárdalo SOLO en variable de entorno:"
echo "     export SONAR_TOKEN=<nuevo_token>"
echo ""
echo "2. 📤 FORZAR PUSH A TODAS LAS RAMAS:"
echo "   git push --force --all origin"
echo "   git push --force --tags origin"
echo ""
echo "3. 👥 NOTIFICAR A COLABORADORES:"
echo "   Todos los colaboradores deben hacer FRESH CLONE:"
echo "   git clone <url-del-repo>"
echo "   (NO hacer git pull, el historial cambió)"
echo ""
echo "4. ✅ VERIFICAR QUE EL SECRETO FUE ELIMINADO:"
echo "   git log --all -S 'sqp_00a1901f' --oneline"
echo "   (No debe aparecer ningún commit)"
echo ""
echo "5. 🔐 CONFIGURAR VARIABLES DE ENTORNO PARA CI/CD:"
echo "   - Agrega SONAR_TOKEN en GitHub Actions Secrets / Render / etc."
echo "   - Nunca vuelvas a hardcodear tokens en archivos del proyecto"
echo ""
echo "=========================================="
echo "  LIMPIEZA COMPLETADA"
echo "=========================================="
