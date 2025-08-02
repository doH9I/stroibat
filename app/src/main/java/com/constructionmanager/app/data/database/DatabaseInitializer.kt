package com.constructionmanager.app.data.database

import com.constructionmanager.app.data.entities.*
import com.constructionmanager.app.utils.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    private val database: ConstructionDatabase
) {
    
    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        // Проверяем, есть ли уже данные
        val usersCount = database.userDao().getActiveUsersCount()
        if (usersCount > 0) return@withContext
        
        // Создаем роли
        createDefaultRoles()
        
        // Создаем пользователей
        createDefaultUsers()
        
        // Создаем организации
        createDefaultOrganizations()
        
        // Создаем виды работ
        createDefaultWorkTypes()
        
        // Создаем демо-проекты
        createDemoProjects()
        
        // Создаем демо-сметы
        createDemoEstimates()
    }
    
    private suspend fun createDefaultRoles() {
        val roles = listOf(
            Role(id = 1, name = "admin", displayName = "Администратор", 
                description = "Полный доступ ко всем функциям", 
                permissions = "[\"*\"]", isSystem = true),
            Role(id = 2, name = "director", displayName = "Генеральный директор", 
                description = "Стратегическое управление", 
                permissions = "[\"projects.view\", \"reports.view\", \"analytics.view\"]"),
            Role(id = 3, name = "manager", displayName = "Менеджер проекта", 
                description = "Управление проектами", 
                permissions = "[\"projects.manage\", \"estimates.manage\", \"reports.create\"]"),
            Role(id = 4, name = "worker", displayName = "Рабочий", 
                description = "Выполнение работ", 
                permissions = "[\"reports.create\", \"photos.upload\", \"timesheet.manage\"]")
        )
        
        roles.forEach { role ->
            database.roleDao().insertRole(role)
        }
    }
    
    private suspend fun createDefaultUsers() {
        val users = listOf(
            createUser("admin", "admin@construction.com", "admin123", "Администратор", "Системы", 1),
            createUser("manager", "manager@construction.com", "manager123", "Иван", "Петров", 3),
            createUser("worker", "worker@construction.com", "worker123", "Сергей", "Иванов", 4)
        )
        
        users.forEach { user ->
            database.userDao().insertUser(user)
        }
    }
    
    private fun createUser(username: String, email: String, password: String, 
                          firstName: String, lastName: String, roleId: Long): User {
        val salt = SecurityUtils.generateSalt()
        val hashedPassword = SecurityUtils.hashPassword(password, salt)
        
        return User(
            username = username,
            email = email,
            passwordHash = hashedPassword,
            salt = salt,
            firstName = firstName,
            lastName = lastName,
            phone = null,
            roleId = roleId,
            isActive = true,
            createdAt = Date(),
            updatedAt = Date()
        )
    }
    
    private suspend fun createDefaultOrganizations() {
        val organizations = listOf(
            Organization(
                id = 1,
                name = "ООО \"СтройИнвест\"",
                fullName = "Общество с ограниченной ответственностью \"СтройИнвест\"",
                type = OrganizationType.CLIENT,
                inn = "7701234567",
                kpp = "770101001",
                ogrn = "1027700000001",
                address = "г. Москва, ул. Строительная, д. 1",
                phone = "+7 (495) 123-45-67",
                email = "info@stroyinvest.ru",
                director = "Иванов Иван Иванович",
                accountant = "Петрова Мария Сергеевна"
            ),
            Organization(
                id = 2,
                name = "ИП Сидоров",
                fullName = "Индивидуальный предприниматель Сидоров Петр Иванович",
                type = OrganizationType.CONTRACTOR,
                inn = "123456789012",
                kpp = null,
                ogrn = "304770000000001",
                address = "г. Москва, ул. Подрядная, д. 5",
                phone = "+7 (495) 987-65-43",
                email = "sidorov@contractor.ru",
                director = "Сидоров Петр Иванович",
                rating = 4.5f
            )
        )
        
        organizations.forEach { org ->
            database.organizationDao().insertOrganization(org)
        }
    }
    
    private suspend fun createDefaultWorkTypes() {
        val workTypes = listOf(
            WorkType(name = "Земляные работы", category = WorkCategory.EARTHWORKS, 
                unit = "м³", basePrice = BigDecimal("500.00")),
            WorkType(name = "Устройство фундамента", category = WorkCategory.FOUNDATION, 
                unit = "м³", basePrice = BigDecimal("3000.00")),
            WorkType(name = "Кладка стен", category = WorkCategory.WALLS, 
                unit = "м²", basePrice = BigDecimal("1500.00")),
            WorkType(name = "Устройство кровли", category = WorkCategory.ROOFING, 
                unit = "м²", basePrice = BigDecimal("2000.00")),
            WorkType(name = "Штукатурка стен", category = WorkCategory.FINISHING, 
                unit = "м²", basePrice = BigDecimal("800.00"))
        )
        
        workTypes.forEach { workType ->
            database.workTypeDao().insertWorkType(workType)
        }
    }
    
    private suspend fun createDemoProjects() {
        val projects = listOf(
            Project(
                id = 1,
                name = "Жилой комплекс \"Солнечный\"",
                description = "Строительство 3-этажного жилого дома",
                address = "г. Москва, ул. Солнечная, д. 10",
                latitude = 55.7558,
                longitude = 37.6176,
                budget = BigDecimal("15000000.00"),
                startDate = Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L),
                plannedEndDate = Date(System.currentTimeMillis() + 180 * 24 * 60 * 60 * 1000L),
                status = ProjectStatus.IN_PROGRESS,
                managerId = 2,
                clientId = 1
            ),
            Project(
                id = 2,
                name = "Офисное здание \"Бизнес-центр\"",
                description = "Строительство 5-этажного офисного здания",
                address = "г. Москва, ул. Деловая, д. 25",
                budget = BigDecimal("25000000.00"),
                startDate = Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L),
                plannedEndDate = Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000L),
                status = ProjectStatus.PLANNING,
                managerId = 2,
                clientId = 1
            )
        )
        
        projects.forEach { project ->
            database.projectDao().insertProject(project)
        }
    }
    
    private suspend fun createDemoEstimates() {
        val totalAmount = BigDecimal("800000.00")
        val vatRate = BigDecimal("0.20")
        val vatAmount = totalAmount.multiply(vatRate)
        val totalWithVat = totalAmount.add(vatAmount)
        
        val estimates = listOf(
            Estimate(
                id = 1,
                projectId = 1,
                name = "Смета на основные работы",
                version = 1,
                totalAmount = totalAmount,
                vatRate = vatRate,
                vatAmount = vatAmount,
                totalWithVat = totalWithVat,
                overheadRate = BigDecimal("0.15"),
                contingencyRate = BigDecimal("0.05"),
                status = EstimateStatus.APPROVED,
                createdBy = 2,
                approvedBy = 1,
                approvedAt = Date()
            )
        )
        
        estimates.forEach { estimate ->
            database.estimateDao().insertEstimate(estimate)
        }
        
        // Создаем элементы сметы
        val estimateItems = listOf(
            EstimateItem(
                estimateId = 1,
                workTypeId = 1,
                description = "Земляные работы",
                quantity = BigDecimal("100.0"),
                unitPrice = BigDecimal("500.00"),
                totalPrice = BigDecimal("50000.00"),
                orderIndex = 1
            ),
            EstimateItem(
                estimateId = 1,
                workTypeId = 2,
                description = "Устройство фундамента",
                quantity = BigDecimal("50.0"),
                unitPrice = BigDecimal("3000.00"),
                totalPrice = BigDecimal("150000.00"),
                orderIndex = 2
            ),
            EstimateItem(
                estimateId = 1,
                workTypeId = 3,
                description = "Кладка стен",
                quantity = BigDecimal("200.0"),
                unitPrice = BigDecimal("1500.00"),
                totalPrice = BigDecimal("300000.00"),
                orderIndex = 3
            ),
            EstimateItem(
                estimateId = 1,
                workTypeId = 4,
                description = "Устройство кровли",
                quantity = BigDecimal("150.0"),
                unitPrice = BigDecimal("2000.00"),
                totalPrice = BigDecimal("300000.00"),
                orderIndex = 4
            )
        )
        
        estimateItems.forEach { item ->
            database.estimateItemDao().insertEstimateItem(item)
        }
    }
}