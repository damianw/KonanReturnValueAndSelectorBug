//
//  ViewController.swift
//  KonanReturnValueAndSelectorBugSampleApp
//
//  Created by Damian Wieczorek on 4/15/18.
//  Copyright © 2018 Damian Wieczorek. All rights reserved.
//

import UIKit
import KonanReturnValueAndSelectorBug

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        let shoppingCart = KRVASBShoppingCart(products: [
            KRVASBProduct(name: "Apples", price: 2.0),
            KRVASBProduct(name: "Oranges", price: 3.0)
        ])
        let firstPrice = shoppingCart.firstPrice()
        let totalCost = shoppingCart.computeTotalCost()
        NSLog("%s", totalCost)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}
