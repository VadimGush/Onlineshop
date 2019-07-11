
 * Маппить явно
 * Избавиться от исключений
 
 ClientDto              ->      registerClient()        ->      AccountDto
 AdminDto               ->      registerAdmin()         ->      AccountDto
 AdminEditDto           ->      editAdmin()             ->      AccountDto
 ClientEditDto          ->      editClient()            ->      AccountDto
 LoginDto               ->      login()                 ->      _
 DepositDto             ->      putDeposit()            ->      AccountDto
 _                      ->      getDeposit()            ->      AccountDto
 ProductDto          ->      buyProduct()            ->      ProductDto
 ProductDto          ->      addToBasket()           ->      List<ProductDto>
 ProductDto          ->      editProductCount()      ->      List<ProductDto>
 _                      ->      getBasket()             ->      List<ProductDto>
 List<ProductDto>    ->      buyBasket()             ->      ResultBasketDto
 CategoryDto            ->      addCategories()         ->      CategoryDto
 _                      ->      getCategoriesById()     ->      CategoryDto
 CategoryDto            ->      editCategories()        ->      CategoryDto
 
 