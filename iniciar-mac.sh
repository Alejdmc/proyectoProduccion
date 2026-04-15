#!/bin/bash

# =====================================================
# Script de inicio para Mac
# Sistema de Taller - Proyecto Producción
# =====================================================

echo ""
echo "========================================"
echo "  Sistema de Taller - Inicio (Mac)"
echo "========================================"
echo ""

# Colores para terminal
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configurar JAVA_HOME para Mac
echo -e "${CYAN}Configurando JAVA_HOME...${NC}"
export JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null)

if [ -z "$JAVA_HOME" ]; then
    echo -e "${RED}✗ Java 21 no encontrado${NC}"
    echo -e "${YELLOW}Por favor instala Java 21 desde:${NC}"
    echo "  https://www.oracle.com/java/technologies/downloads/"
    echo ""
    read -p "Presiona Enter para salir..."
    exit 1
fi

echo -e "${GREEN}✓ JAVA_HOME: $JAVA_HOME${NC}"
echo ""

# Verificar que existe database.properties
if [ ! -f "database.properties" ]; then
    echo -e "${YELLOW}⚠ No se encontró database.properties${NC}"
    echo ""

    if [ -f "database.properties.mac" ]; then
        echo -e "${GREEN}Se encontró database.properties.mac${NC}"
        read -p "¿Deseas crear database.properties desde el ejemplo de Mac? (s/n): " respuesta

        if [ "$respuesta" = "s" ] || [ "$respuesta" = "S" ]; then
            cp database.properties.mac database.properties
            echo -e "${GREEN}✓ Archivo database.properties creado${NC}"
            echo ""
            echo -e "${YELLOW}IMPORTANTE: Edita database.properties y configura tus credenciales${NC}"
            echo ""
            read -p "¿Deseas editar el archivo ahora? (s/n): " editar
            if [ "$editar" = "s" ] || [ "$editar" = "S" ]; then
                nano database.properties
                echo ""
                read -p "Presiona Enter después de guardar los cambios..."
            fi
        fi
    else
        echo -e "${CYAN}Creando database.properties con valores por defecto (XAMPP Mac)...${NC}"
        cat > database.properties << 'EOF'
# Configuración de Conexión MySQL para Mac
db.host=localhost
db.port=3306
db.name=taller_db
db.user=root
db.password=
EOF
        echo -e "${GREEN}✓ Archivo creado con valores por defecto${NC}"
    fi
    echo ""
fi

# Mostrar configuración actual
echo -e "${CYAN}Configuración actual:${NC}"
if [ -f "database.properties" ]; then
    grep -v "^#" database.properties | grep -v "^$" | while read line; do
        if echo "$line" | grep -q "password"; then
            echo "  db.password=***"
        else
            echo "  $line"
        fi
    done
fi
echo ""

# Verificar MySQL
echo -e "${YELLOW}Verificando MySQL...${NC}"
if pgrep -x "mysqld" > /dev/null; then
    echo -e "${GREEN}✓ MySQL está corriendo${NC}"
else
    echo -e "${YELLOW}⚠ MySQL NO está corriendo${NC}"
    echo ""
    echo "Opciones para iniciar MySQL en Mac:"
    echo ""
    echo "1. XAMPP - Interfaz gráfica:"
    echo "   Abre XAMPP desde Applications → Start MySQL"
    echo ""
    echo "2. XAMPP - Terminal:"
    echo "   sudo /Applications/XAMPP/xamppfiles/xampp startmysql"
    echo ""
    echo "3. Homebrew:"
    echo "   brew services start mysql"
    echo ""
    read -p "¿Deseas intentar iniciar XAMPP ahora? (s/n): " iniciar

    if [ "$iniciar" = "s" ] || [ "$iniciar" = "S" ]; then
        if [ -d "/Applications/XAMPP" ]; then
            echo -e "${CYAN}Iniciando XAMPP MySQL...${NC}"
            sudo /Applications/XAMPP/xamppfiles/xampp startmysql
            echo ""
            sleep 3

            if pgrep -x "mysqld" > /dev/null; then
                echo -e "${GREEN}✓ MySQL iniciado correctamente${NC}"
            else
                echo -e "${RED}✗ No se pudo iniciar MySQL${NC}"
                echo "Intenta iniciarlo manualmente desde XAMPP"
            fi
        else
            echo -e "${RED}✗ XAMPP no encontrado en /Applications/XAMPP${NC}"
            echo "Inicia MySQL manualmente"
        fi
        echo ""
        read -p "Presiona Enter cuando MySQL esté corriendo..."
    fi
fi

echo ""
echo "========================================"
echo -e "  ${GREEN}Iniciando aplicación...${NC}"
echo "========================================"
echo ""

# Ejecutar la aplicación
./mvnw javafx:run

if [ $? -ne 0 ]; then
    echo ""
    echo -e "${RED}✗ Error al ejecutar la aplicación${NC}"
    echo ""
    read -p "Presiona Enter para salir..."
fi

