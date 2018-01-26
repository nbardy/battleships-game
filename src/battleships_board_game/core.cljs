(ns battleships-board-game.core
  (:require
    #_[om.core :as om :include-macros true]
    [sablono.core :as sab :include-macros true])
  (:require-macros
    [battleships-board-game.card-types :refer
     [def-map-tiles def-ship-cards def-item-cards]]
    [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(def-map-tiles standard-map-tiles
  < > S S
  S < r S
  S S < >
  S V S <

  S A A S
  S S r S
  V S S S
  S V V S

  R R < <
  R < S V
  S S S S
  S r S A

  S A S <
  S S < S
  S < S V
  < S S S

  S r R S
  r S S S
  S < r S
  S S S S

  S < < <
  < > S r
  < S > S
  S S S S

  > S V S
  R S S S
  S < A S
  S S r S

  S S A V
  A V S V
  A S < R
  < < R S

  S S S S
  S R S r
  S r S S
  S S S S

  R R S S
  R R S A
  S S S S
  S > S S

  S V V S
  > S S <
  > S S <
  S A A S

  R R r S
  S R R S
  S r r S
  S S S S

  S r S S
  S > V S
  S A < S
  S S S S

  V < < <
  V r r A
  V r r A
  > > > A

  V < S S
  V S < S
  r r S <
  > > R A

  S R S S
  S R S R
  r S S S
  S S S r

  S S S r
  S S S r
  S S S R
  r r R R

  S A S A
  A V S V
  V S A S
  S S V S

  < < S >
  < S S r
  > r S R
  S R S S

  > > < <
  S S r r
  > > R S
  R R S S

  A < R S
  S S S S
  S V S V
  S S S S

  S S S S
  S S S S
  S S S S
  S S S S

  S r S S
  S S < S
  S S S >
  S S S >

  S S S S
  < S S S
  S S S r
  S S S r

  r R S S
  R A S S
  S S S S
  S > S S

  S S S S
  S S S S
  S S S S
  S S S S

  S S r S
  S S S S
  R S < V
  S S A >

  S S S S
  S S S <
  S S S >
  S S S >

  S S S S
  S r S S
  S r S S
  S S > S
)

(def-ship-cards round1-ships
  "Dinghey" 8 25 "Generic as fuck"
  "Dinghey" 8 25 "Generic as fuck"
  "Dinghey" 8 25 "Generic as fuck")

(def-ship-cards round2-ships
  "Ripper" 7 22 "Unaffected by currents"
  "Swift" 6 25 "Can pivot once per round"
  "Gunner" 6 20 "Can double shoot cannonballs")

(def-ship-cards round3-ships
  "Sniper" 6 30 "+1 range"
  "Luger" 6 10 "Shoots one free fiery cannonball per round"
  "Juggernaut" 15 20 "Can only move three times per round")

(def-item-cards round2-items
  "Fire" "+2 flaming cannonballs per round"
  "Extra Stock" "+10 small balls per round"
  "Double Hull" "+4 Health on ship")

(def-item-cards round3-items
  "Gold Rudder" "Can spin twice per round"
  "Floatsam" "Can drop floatsam twice per round"
  "Shaman" "Can rotate a tile once per round")

(def-item-cards round1-items
  "Blank" "Nothing")

(def all-items
  (concat #_round1-items
          round2-items
          round3-items))

(def all-ships
  (concat round1-ships
          round2-ships
          round3-ships))

(def tile-size 50)
(defn square [text & {:as opts }]
  [:td
   {:style
    {:width (or (:size opts) tile-size)
     :height (or (:size opts) tile-size)
     :position "relative"
     :margin 0
     :padding 0
     :overflow "hidden"
     :color "white"
     :font-style "bold"
     :line-height 1
     ;:border "solid 1px white"
     :background-color (:color opts)}}
   [:div
    {:style
     {:position "absolute"
      :top 0
      :left 0
      :font-size 35}}
    text]])

(defn circle [& {:as opts}]
  [:div
   {:style
    {:background-image (if (opts :gradient) (opts :gradient))
     :background-color (opts :color)
     :position "absolute"
     :width (or (opts :size) tile-size)
     :height (or (opts :size) tile-size)
     :border-radius "50%"}}
   (:text opts)])

(def left-turn [:img {:src "img/left-turn.svg" :width 50
                      :style {:margin-top 5}}])
(def right-turn [:img {:src "img/left-turn.svg" :width 50
                       :style {:margin-top 5
                               :transform "scaleX(-1)"}}])
(def straight [:img {:src "img/up-arrow.svg" :width 50 :height 50
                     :style {:margin-left 0}}])
(def player-colors ["red" "orange" "purple" "green"])

(defn n-of-colors [tile n colors]
  (mapcat
    #(repeat n (square tile :color %))
    colors))

(def movement-tokens
  (mapcat
    #(n-of-colors % 15 player-colors)
    [left-turn right-turn straight]))


(defn ship-marker [n & {:keys [color] :as opts }]
  (sab/html
    [:svg {:width tile-size :height (+ tile-size 10)}
     [:polygon {:points "0,40 15,55 30,40"
                :style {:fill color}}]
     [:rect {:width 30 :height 40 :style {:fill color}}]
     [:text {:x 8
             :y 30
             :style {:font-size 24 :font-weight "bold"}
             :width tile-size :fill "white"
             :height (+ tile-size 10)}
      n]
     ]))

(def ship-markers
  (for [color player-colors]
     (for [n [1 2 3]]
       [:div  {:position "relative"}
        [:div {:style {:position "relative" :padding-left 0}}
         (circle :color color)
         [:span {:style {:color "white" :font-size 24
                         :position "absolute" :top 8 :left 18}} n]]
        [:div {:style {:margin-left 50}}
         (ship-marker n :color color)]])))

(def cannonballs
  (repeat 200
          (circle :size 27
                  :gradient "radial-gradient(#000000,#333)")))

(def fiery-cannonballs
  (repeat 20 (sab/html [:img {:width 40 :height 40
                              :src "img/fiery-cannonball.png"}])))
(def floatsam
  (repeat 5 (sab/html [:img {:width 40 :height 40
                             :src "img/floatsam.png"}])))

(def tokens
  (concat
    cannonballs
    ship-markers
    ; Movement tokens
    movement-tokens))


(defn render-map-square [item]
  (case item
    "<" (square "\u2190" :color "blue")
    ">" (square "\u2192" :color "blue")
    "V" (square "\u2193" :color "blue")
    "A" (square "\u2191" :color "blue")
    "R" (square
          (sab/html [:img {:src "img/big-rock.png"
                           :width 52
                           :height 46
                           :padding-top 2}])
          :color "blue")
    "r" (square
          (sab/html [:img {:src "img/small-rock.png"
                           :width 30
                           :height 30
                           :style {:padding-left 10
                                   :padding-top 10}}])
          :color "blue")
    "S" (square " " :color "blue")
    (square item :color "blue")))

(defn render-map-tile [tile]
  (sab/html
    [:table
     {:style
      {:border-spacing 3
       :background-color "#99E0D7"
       ;:border-collapse "collapse"
       }}
     [:tbody
     (for [row tile]
       [:tr
        (for [square row]
          (render-map-square square))])]]))


;; (defcard tile-data
;;   standard-map-tiles)


(defn render-grid [items render-function]
  (sab/html
    [:div
     {:style
      {:display "inline"}}
     (for [item items]
       [:div
        {:style
         {:display "inline-block"
          }}
        (render-function item)])]))

(defcard all-tiles
  (render-grid standard-map-tiles render-map-tile))

(defn render-item-card [ship]
  (sab/html
    [:div
     {:style
      {:border "5px solid black"
       :border-radius 5
       :background "-webkit-linear-gradient(top, #3C5A99, #3A7856)"
       :position "relative"
       :width 250
       :height 300}}
     [:img
      {:src "img/item.jpg"
       :style
       {:position "relative"
        :background-color "white"
        :text-align "center"
        :vertical-align "middle"
        :left "50%"
        :margin-left -50
        :top 20
        :height 100
        :width 100}
       :alt (str "Photo of a " (ship :name))}]
     [:div
      {:style
       {:background-color "white"
        :text-align "center"
        :position "absolute"
        :width 120
        :margin-left -60
        :left "50%"
        :top 140
        :height 20}}
        (ship :name)]
     [:div
      {:style
       {:background-color "white"
        :text-align "center"
        :position "absolute"
        :vertical-align "middle"
        ;:line-height "100px"
        :width 200
        :margin-left -100
        :left "50%"
        :top 180
        :height 100}}
        (ship :description)]
     ]))

(defn render-ship-card [ship]
  (sab/html
    [:div
     {:style
      {:border "5px solid black"
       :border-radius 5
       :background "-webkit-linear-gradient(top, #7A3E24, #C1A225)"
       :position "relative"
       :width 250
       :height 300}}
     [:img
      {:src "img/ship.jpg"
       :style
       {:position "relative"
        :background-color "white"
        :text-align "center"
        :vertical-align "middle"
        :left "50%"
        :margin-left -50
        :top 20
        :height 100
        :width 100}
       :alt (str "Photo of a " (ship :name))}]
     [:div
      {:style
       {:background-color "grey"
        :border-radius "50%"
        :text-align "center"
        :position "absolute"
        :right 5
        :top 5
        :width 20
        :height 20}}
        (ship :cbs)]
     [:div
      {:style
       {:background-color "white"
        :text-align "center"
        :position "absolute"
        :width 120
        :margin-left -60
        :left "50%"
        :top 140
        :height 20}}
        (ship :name)]
     [:div
      {:style
       {:position "absolute"
        :background-color "red"
        :text-align "center"
        :top 5
        :left 5}}
      (circle :text (ship :health) :color "red" :size 20)]
     [:div
      {:style
       {:background-color "white"
        :text-align "center"
        :position "absolute"
        :vertical-align "middle"
        ;:line-height "100px"
        :width 200
        :margin-left -100
        :left "50%"
        :top 180
        :height 100}}
        (ship :description)]
     ]))

(defcard ship-cards
  (render-grid all-ships render-ship-card))

(defcard item-cards
  (render-grid all-items render-item-card))

(defn spacer [w h]
  (fn [i]
    (sab/html
      [:div
       {:style
        {:width w
         :height h}}
       i])))
(defcard balls
  (sab/html
    [:div {}
     [:div {}
      (render-grid fiery-cannonballs (spacer 25 20))]
     [:div {:style {:margin-bottom 5}}
      (render-grid cannonballs (spacer 27 21))]
     [:div {}
      (render-grid movement-tokens identity)]
      [:div {}
       (render-grid ship-markers (spacer 80 100)) ]]))

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (js/React.render (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

